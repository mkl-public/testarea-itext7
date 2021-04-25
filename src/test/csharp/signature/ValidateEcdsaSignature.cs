using iText.Kernel.Pdf;
using iText.Signatures;
using NUnit.Framework;
using System;
using System.Collections.Generic;

namespace iText7.Net_Playground.Signature
{
    public class ValidateEcdsaSignature
    {
        [SetUp]
        public void Setup()
        {
        }

        /// <summary>
        /// ECDSA signed PDF fails signature verification with iText 7 (C#), but succeeds with Adobe Reader DC
        /// https://stackoverflow.com/questions/67214674/ecdsa-signed-pdf-fails-signature-verification-with-itext-7-c-but-succeeds-wi
        /// sample_signed_ecdsa.pdf
        /// https://drive.google.com/drive/folders/1dTa8i2T7Fs-ibTTPOdC9Gb527S7-EeeO?usp=sharing
        /// 
        /// Indeed, the signature does not validate.
        /// 
        /// A more in-detail analysis shows that the ECDSA signature value is encoded
        /// using plain format while iText assumes a TLV encoded value. (The signature
        /// algorithm which usually implies the format here is incorrectly set to the
        /// OID of ECDSA public keys in the signed PDF at hand, not a specific signature
        /// algorithm at all.)
        /// </summary>
    [Test]
        public void ValidateSampleSignedEcdsa()
        {
            PdfDocument pdfDoc = new PdfDocument(new PdfReader(@"..\..\..\src\test\resources\mkl\testarea\itext7\signature\sample_signed_ecdsa.pdf"));
            SignatureUtil signUtil = new SignatureUtil(pdfDoc);
            IList<string> names = signUtil.GetSignatureNames();
            foreach (string name in names)
            {
                PdfPKCS7 pkcs7 = signUtil.ReadSignatureData(name);
                bool wholeDocument = signUtil.SignatureCoversWholeDocument(name);
                bool signatureIntegrityAndAuthenticity = pkcs7.VerifySignatureIntegrityAndAuthenticity(); // this returns false, even though Adobe has no problem verifying the signature.
                                                                                                          // more code to read values and put them in a json
                Console.WriteLine("Integrity and authenticity: {0}\nCovers whole document: {1}", signatureIntegrityAndAuthenticity, wholeDocument);
            }
        }
    }
}