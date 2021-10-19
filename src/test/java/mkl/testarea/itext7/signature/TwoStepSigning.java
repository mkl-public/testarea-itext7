package mkl.testarea.itext7.signature;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignatureContainer;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;

/**
 * @author mkl
 */
public class TwoStepSigning
{
    final static File RESULT_FOLDER = new File("target/test-outputs", "signature");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
    }

    /**
     * <a href="http://stackoverflow.com/questions/39151230/in-itext-7-how-to-sign-a-pdf-with-2-steps">
     * In Itext 7, how to sign a pdf with 2 steps?
     * </a>
     * <br/>
     * <a href="https://1drv.ms/b/s!AkF6t4TavwMvdLmiyQlYVcCLqw4">
     * document_in.pdf
     * </a>
     * <p>
     * Indeed, the result of this PreSign code is broken for the sample file.
     * The reason is that in parallel to the original file iText tries to use
     * object streams as much as possible, unfortunately even for the signature
     * dictionary which is devastatingly wrong.
     * </p>
     */
    @Test
    public void testPreSignDocumentIn() throws IOException, GeneralSecurityException
    {
        try (   InputStream resource = getClass().getResourceAsStream("document_in.pdf");
                FileOutputStream os = new FileOutputStream(new File(RESULT_FOLDER, "document_in-presigned.pdf")) )
        {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            Certificate maincertificate = cf.generateCertificate(getClass().getResourceAsStream("Samson_aut.cer"));
            Certificate[] chain = new Certificate[] { maincertificate };
            String hashAlgorithm = "SHA-256";

            PdfReader reader = new PdfReader(resource);
            PdfSigner signer = new PdfSigner(reader, os, new StampingProperties());
            signer.setFieldName("certification"); // this field already exists
            signer.setCertificationLevel(PdfSigner.CERTIFIED_FORM_FILLING);
            PdfSignatureAppearance sap = signer.getSignatureAppearance();
            sap.setReason("Certification of the document");
            sap.setLocation("On server");
            sap.setCertificate(maincertificate);
            BouncyCastleDigest digest = new BouncyCastleDigest();
            PdfPKCS7 sgn = new PdfPKCS7(null, chain, hashAlgorithm, null, digest,false);
            PreSignatureContainer external = new PreSignatureContainer(PdfName.Adobe_PPKLite,PdfName.Adbe_pkcs7_detached);
            signer.signExternalContainer(external, 8192);
            byte[] hash=external.getHash();
            sgn.getAuthenticatedAttributeBytes(hash, PdfSigner.CryptoStandard.CMS, null, null);
        }
    }

    public class PreSignatureContainer implements IExternalSignatureContainer
    {
        private PdfDictionary sigDic;
        private byte hash[];

        public PreSignatureContainer(PdfName filter, PdfName subFilter)
        {
            sigDic = new PdfDictionary();
            sigDic.put(PdfName.Filter, filter);
            sigDic.put(PdfName.SubFilter, subFilter);
        }

        @Override
        public byte[] sign(InputStream data) throws GeneralSecurityException
        {
            String hashAlgorithm = "SHA256";
            BouncyCastleDigest digest = new BouncyCastleDigest();

            try
            {
                this.hash = DigestAlgorithms.digest(data, digest.getMessageDigest(hashAlgorithm));
            }
            catch (IOException e)
            {
                throw new GeneralSecurityException("PreSignatureContainer signing exception", e);
            }

            return new byte[0];
        }

        @Override
        public void modifySigningDictionary(PdfDictionary signDic)
        {
            signDic.putAll(sigDic);
        }

        public byte[] getHash()
        {
            return hash;
        }

        public void setHash(byte hash[])
        {
            this.hash = hash;
        }
    }
}
