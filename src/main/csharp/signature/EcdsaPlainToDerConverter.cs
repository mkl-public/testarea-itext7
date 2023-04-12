using iText.Forms;
using iText.Kernel.Pdf;
using iText.Signatures;
using Org.BouncyCastle.Asn1;
using Org.BouncyCastle.Cms;
using System;
using System.Collections.Generic;
using System.IO;
using Org.BouncyCastle.Asn1.Cms;
using System.Security.Cryptography;
using iText.IO.Source;

namespace iText7.Net_Playground.Signature
{
    public class EcdsaPlainToDerConverter
    {
        private FileStream input = null;
        private Stream output = null;
        private PdfDocument pdf = null;
        private SignatureUtil sigUtil = null;
        private static string outFileStr = "";

        public static void Run(string file)
        {
            outFileStr = file.Substring(0, file.Length - 4) + "-converted.pdf";
            var inStream = new FileStream(file, FileMode.Open);
            File.Delete(outFileStr);
            File.Copy(file, outFileStr);
            inStream.Position = 0;
            var outStream = new FileStream(outFileStr, FileMode.OpenOrCreate, FileAccess.ReadWrite);

            var main = new EcdsaPlainToDerConverter(inStream, outStream);
            var signatureNames = main.GetSignatureNames();
            IList<string> fixedSignatures = new List<string>();

            foreach (var name in signatureNames)
                if (main.Convert(name))
                    fixedSignatures.Add(name);

            if (fixedSignatures.Count == 0)
                Console.Error.WriteLine("!!! failed to convert any");
            else
                Console.WriteLine("converted sigs");

        }

        public EcdsaPlainToDerConverter(FileStream input, Stream output)
        {
            this.input = input;
            this.output = output;
            this.pdf = new PdfDocument(new PdfReader(this.input));
            sigUtil = new SignatureUtil(pdf);
        }

        public IList<string> GetSignatureNames()
        {
            return sigUtil.GetSignatureNames();
        }

        public bool Convert(string signatureName)
        {
            var fields = PdfAcroForm.GetAcroForm(pdf, false);
            var sigDict = sigUtil.GetSignatureDictionary(signatureName);
            if (sigDict == null)
            {
                Console.Error.WriteLine("!!! Sig Field has no value, not signed");
                return false;
            }

            var contents = sigDict.GetAsString(PdfName.Contents);
            if (contents == null)
            {
                Console.Error.WriteLine("!!! Sig Field value does not have content, not signed");
                return false;
            }

            var contentBytes = contents.GetValueBytes();

            CmsSignedData signedData;
            try
            {
                signedData = new CmsSignedData(contentBytes);
            }
            catch (CmsException ex)
            {
                Console.Error.WriteLine("!!! Sig Field could not be parsed as CMS signed signature");
                throw;
            }

            IList<SignerInfo> signerinfos = new List<SignerInfo>();
            bool converted = false;


            foreach (SignerInformation signerInfo in signedData.GetSignerInfos().GetSigners())
            {
                if ("1.2.840.10045.2.1".Equals(signerInfo.EncryptionAlgOid))
                {
                    String digestAlgorithmName = new Oid(signerInfo.DigestAlgOid).FriendlyName;
                    byte[] signatureValue;
                    if (IsStandardEncoding(signerInfo.GetSignature()))
                    {
                        signatureValue = signerInfo.GetSignature();
                    }
                    else
                    {
                        signatureValue = EcdsaFormatHelper.PlainToDer(signerInfo.GetSignature());
                        converted = true;
                    }

                    SignerInfo sigInfo = signerInfo.ToSignerInfo();
                    SignerInfo sigInfoFixed = new SignerInfo(sigInfo.SignerID, sigInfo.DigestAlgorithm,
                        sigInfo.AuthenticatedAttributes, sigInfo.DigestEncryptionAlgorithm,
                        new DerOctetString(signatureValue), sigInfo.UnauthenticatedAttributes);
                    signerinfos.Add(sigInfoFixed);
                }
                else
                {
                    signerinfos.Add(signerInfo.ToSignerInfo());
                }
            }

            if (converted)
            {
                PdfArray b = sigDict.GetAsArray(PdfName.ByteRange);
                long[] gaps = b.ToLongArray();
                int spaceAvailable = (int)(gaps[2] - gaps[1]) - 2;
                try
                {
                    signedData = Rebuild(signedData, signerinfos);
                }
                catch (CmsException ex)
                {
                    Console.WriteLine(ex);
                    return false;
                }

                try
                {
                    contentBytes = signedData.GetEncoded();
                }
                catch (IOException ex)
                {
                    Console.WriteLine(ex);
                    return false;
                }

                byte[] dataToWrite;
                ByteBuffer bb;
                try
                {
                    bb = new ByteBuffer(spaceAvailable);
                    foreach (byte bi in contentBytes)
                        bb.AppendHex(bi);
                    int remain = (spaceAvailable - contentBytes.Length * 2);
                    for (int i = 0; i < remain; i++)
                        bb.Append((byte)48);

                    dataToWrite = bb.ToByteArray();
                }
                catch (IOException e)
                {
                    Console.WriteLine(e);
                    return false;
                }

                try
                {
                    var offset = output.Seek(gaps[1] + 1, SeekOrigin.Begin);
                    output.Write(dataToWrite, 0, dataToWrite.Length);
                    output.Close();
                }
                catch (Exception e)
                {
                    Console.WriteLine(e);
                    return false;
                }
            }

            return converted;
        }

        private CmsSignedData Rebuild(CmsSignedData signedData, IList<SignerInfo> signerinfos)
        {
            Asn1EncodableVector vec = new Asn1EncodableVector();
            foreach (var signerInfo in signerinfos)
                vec.Add(signerInfo);
            Asn1Set signers = new DerSet(vec);
            Asn1Sequence sD = (Asn1Sequence)signedData.ContentInfo.Content.ToAsn1Object();

            vec = new Asn1EncodableVector();
            for (int i = 0; i < sD.Count; i++)
                vec.Add(sD[i]);
            vec.Add(signers);
            var sigData = SignedData.GetInstance(new BerSequence(vec));
            ContentInfo contentInfo = new ContentInfo(signedData.ContentInfo.ContentType, sigData);
            signedData = new CmsSignedData(contentInfo);
            return signedData;
        }

        bool IsStandardEncoding(byte[] signatureBytes)
        {
            try
            {
                Asn1Object prim = Asn1Object.FromByteArray(signatureBytes);
                if (prim is Asn1Sequence)
                {
                    Asn1Sequence seq = (Asn1Sequence)prim;
                    if (seq.Count == 2)
                    {
                        return (seq[0] is DerInteger) && (seq[1] is DerInteger);
                    }
                }
            }
            catch (IOException ex)
            {
                Console.WriteLine("could not be parsed as BER -> not standard");
            }

            return false;
        }
    }
}
