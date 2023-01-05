package mkl.testarea.itext7.signature;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.List;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.signatures.CertificateInfo;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.SignatureUtil;

/**
 * @author mkl
 */
public class VerifySignature {
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        BouncyCastleProvider bcp = new BouncyCastleProvider();
        Security.insertProviderAt(bcp, 1);
    }

    /**
     * <a href="https://stackoverflow.com/questions/48285453/verifying-certificate-of-signed-and-secured-pdf-in-itext-pdf-java">
     * Verifying certificate of signed and secured PDF in iText PDF Java
     * </a>
     * <br/>
     * <a href="https://drive.google.com/drive/folders/1KAqHUh-Iij0I4WXJUCx-rMd8FQFq5tCe?usp=sharing">
     * pdf-sample-signed.pdf
     * </a>
     * <p>
     * The PDF is both signed and encrypted. In contrast to iText 5 the iText 7
     * code prevents "decryption" of the values of the <b>Contents</b> key in
     * signature dictionaries. Thus, the parsing of this "decrypted" signature
     * container can succeed.
     * </p>
     */
    @Test
    public void testVerifyPdfSampleSigned() throws IOException, GeneralSecurityException {
        System.out.println("\n\npdf-sample-signed.pdf\n===================");
        
        try (   InputStream resource = getClass().getResourceAsStream("pdf-sample-signed.pdf") ) {
            PdfDocument pdfDoc = new PdfDocument(new PdfReader(resource, new ReaderProperties().setPassword("password".getBytes())));
            SignatureUtil signUtil = new SignatureUtil(pdfDoc);
            List<String> names = signUtil.getSignatureNames();
            for (String name : names) {
                System.out.println("===== " + name + " =====");
                System.out.println("Signature covers whole document: " + signUtil.signatureCoversWholeDocument(name));
                System.out.println("Document revision: " + signUtil.getRevision(name) + " of " + signUtil.getTotalRevisions());
                PdfPKCS7 pkcs7 = signUtil.readSignatureData(name);
                System.out.println("Subject: " + CertificateInfo.getSubjectFields(pkcs7.getSigningCertificate()));
                System.out.println("Integrity check OK? " + pkcs7.verifySignatureIntegrityAndAuthenticity());
            }
            System.out.println();
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/63398930/pdf-signature-ignored-by-acrobat-but-visible-in-other-validation-tools">
     * PDF signature ignored by Acrobat but visible in other validation tools
     * </a>
     * <br/>
     * <a href="https://easyupload.io/2lrfg8">
     * rfc6455 (3).pdf
     * </a>
     * <p>
     * Apparently this file can be validated by iText.
     * </p>
     */
    @Test
    public void testVerifyRfc6455() throws IOException, GeneralSecurityException {
        System.out.println("\n\nrfc6455 (3).pdf\n===================");
        
        try (   InputStream resource = getClass().getResourceAsStream("rfc6455 (3).pdf") ) {
            PdfDocument pdfDoc = new PdfDocument(new PdfReader(resource, new ReaderProperties().setPassword("password".getBytes())));
            SignatureUtil signUtil = new SignatureUtil(pdfDoc);
            List<String> names = signUtil.getSignatureNames();
            for (String name : names) {
                System.out.println("===== " + name + " =====");
                System.out.println("Signature covers whole document: " + signUtil.signatureCoversWholeDocument(name));
                System.out.println("Document revision: " + signUtil.getRevision(name) + " of " + signUtil.getTotalRevisions());
                PdfPKCS7 pkcs7 = signUtil.readSignatureData(name);
                System.out.println("Subject: " + CertificateInfo.getSubjectFields(pkcs7.getSigningCertificate()));
                System.out.println("Integrity check OK? " + pkcs7.verifySignatureIntegrityAndAuthenticity());
            }
            System.out.println();
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/67214674/ecdsa-signed-pdf-fails-signature-verification-with-itext-7-c-but-succeeds-wi">
     * ECDSA signed PDF fails signature verification with iText 7 (C#), but succeeds with Adobe Reader DC
     * </a>
     * <br/>
     * <a href="https://drive.google.com/drive/folders/1dTa8i2T7Fs-ibTTPOdC9Gb527S7-EeeO?usp=sharing">
     * sample_signed_ecdsa.pdf
     * </a>
     * <p>
     * Indeed, the signature does not validate.
     * </p>
     * <p>
     * A more in-detail analysis shows that the ECDSA signature value is encoded
     * using plain format while iText assumes a TLV encoded value. (The signature
     * algorithm which usually implies the format here is incorrectly set to the
     * OID of ECDSA public keys in the signed PDF at hand, not a specific signature
     * algorithm at all.)
     * </p>
     */
    @Test
    public void testVerifySampleSignedEcdsa() throws IOException, GeneralSecurityException {
        System.out.println("\n\nsample_signed_ecdsa.pdf\n===================");

        try (   InputStream resource = getClass().getResourceAsStream("sample_signed_ecdsa.pdf") ) {
            PdfDocument pdfDoc = new PdfDocument(new PdfReader(resource));
            SignatureUtil signUtil = new SignatureUtil(pdfDoc);
            List<String> names = signUtil.getSignatureNames();
            for (String name : names) {
                System.out.println("===== " + name + " =====");
                System.out.println("Signature covers whole document: " + signUtil.signatureCoversWholeDocument(name));
                System.out.println("Document revision: " + signUtil.getRevision(name) + " of " + signUtil.getTotalRevisions());
                PdfPKCS7 pkcs7 = signUtil.readSignatureData(name);
                System.out.println("Subject: " + CertificateInfo.getSubjectFields(pkcs7.getSigningCertificate()));
                System.out.println("Integrity check OK? " + pkcs7.verifySignatureIntegrityAndAuthenticity());
            }
            System.out.println();
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/75005703/adobe-acrobat-reader-and-foxit-reader-show-pdf-signature-valid-but-itext7-says-o">
     * Adobe Acrobat Reader and Foxit reader show PDF Signature Valid but iText7 says otherwise
     * </a>
     * <br/>
     * <a href="https://drive.google.com/file/d/1bvk5EckUrJ-dzehhQJyBGkM4QLrIF3Uk/view?usp=sharing">
     * 3-HD-LAO-DONG-17.50.25-29.03.2022 (3).pdf
     * </a>
     * <p>
     * Indeed, the second signature fails to validate in iText. As it turns
     * out, the CMS container of that signature has an <code>eContent</code>
     * element in its <code>EncapsulatedContentInfo </code>, and iText uses
     * it in its validation code. On the other hand, this element contains an
     * empty octet string which makes no sense at all in a PDF signature, so
     * one can also understand that lax validators ignore it.
     * </p>
     */
    @Test
    public void testVerify3HdLaoDong17_50_25_29_03_2022() throws IOException, GeneralSecurityException {
        System.out.println("\n\n3-HD-LAO-DONG-17.50.25-29.03.2022 (3)\n===================");

        try (   InputStream resource = getClass().getResourceAsStream("3-HD-LAO-DONG-17.50.25-29.03.2022 (3).pdf") ) {
            PdfDocument pdfDoc = new PdfDocument(new PdfReader(resource));
            SignatureUtil signUtil = new SignatureUtil(pdfDoc);
            List<String> names = signUtil.getSignatureNames();
            for (String name : names) {
                System.out.println("===== " + name + " =====");
                System.out.println("Signature covers whole document: " + signUtil.signatureCoversWholeDocument(name));
                System.out.println("Document revision: " + signUtil.getRevision(name) + " of " + signUtil.getTotalRevisions());
                PdfPKCS7 pkcs7 = signUtil.readSignatureData(name);
                System.out.println("Subject: " + CertificateInfo.getSubjectFields(pkcs7.getSigningCertificate()));
                System.out.println("Integrity check OK? " + pkcs7.verifySignatureIntegrityAndAuthenticity());
            }
            System.out.println();
        }
    }
}
