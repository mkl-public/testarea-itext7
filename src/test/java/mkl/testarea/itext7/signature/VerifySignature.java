/**
 * 
 */
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
 * @author mklink
 *
 */
public class VerifySignature {

    /**
     * @throws java.lang.Exception
     */
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
                PdfPKCS7 pkcs7 = signUtil.verifySignature(name);
                System.out.println("Subject: " + CertificateInfo.getSubjectFields(pkcs7.getSigningCertificate()));
                System.out.println("Integrity check OK? " + pkcs7.verify());
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
                PdfPKCS7 pkcs7 = signUtil.verifySignature(name);
                System.out.println("Subject: " + CertificateInfo.getSubjectFields(pkcs7.getSigningCertificate()));
                System.out.println("Integrity check OK? " + pkcs7.verify());
            }
            System.out.println();
        }
    }
}
