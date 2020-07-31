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
import com.itextpdf.signatures.CertificateInfo;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.SignatureUtil;

/**
 * <p>
 * This test class checks how iText 7 validation reacts to the example files
 * for the "shadow attacks" provided by Ruhr Uni Bochum PDF insecurity site
 * https://www.pdf-insecurity.org/signature-shadow/shadow-attacks.html
 * </p>
 * <p>
 * As it turns out (and as was to be expected by the description of the
 * attack), iText recognizes in all cases that the signature does not
 * cover the whole document which implies that (allowed or disallowed)
 * changes have been made.
 * </p>
 * 
 * @author mkl
 */
public class ShadowAttacks {
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        BouncyCastleProvider bcp = new BouncyCastleProvider();
        Security.insertProviderAt(bcp, 1);
    }

    @Test
    public void testVerifyHideShadowFileSigned() throws IOException, GeneralSecurityException {
        System.out.println("\n\nhide-shadow-file-signed.pdf\n======");
        try (   InputStream resource = getClass().getResourceAsStream("hide-shadow-file-signed.pdf");
                PdfDocument document = new PdfDocument(new PdfReader(resource)) ) {
            SignatureUtil signatureUtil = new SignatureUtil(document);
            List<String> names = signatureUtil.getSignatureNames();
            for (String name : names) {
                System.out.println("===== " + name + " =====");
                System.out.println("Signature covers whole document: " + signatureUtil.signatureCoversWholeDocument(name));
                System.out.println("Document revision: " + signatureUtil.getRevision(name) + " of " + signatureUtil.getTotalRevisions());
                PdfPKCS7 pkcs7 = signatureUtil.readSignatureData(name);
                System.out.println("Subject: " + CertificateInfo.getSubjectFields(pkcs7.getSigningCertificate()));
                System.out.println("Integrity check OK? " + pkcs7.verifySignatureIntegrityAndAuthenticity());
            }
            System.out.println();
        }
    }

    @Test
    public void testVerifyreplaceShadowFileSignedManipulated() throws IOException, GeneralSecurityException {
        System.out.println("\n\nreplace-shadow-file-signed-manipulated.pdf\n======");
        try (   InputStream resource = getClass().getResourceAsStream("replace-shadow-file-signed-manipulated.pdf");
                PdfDocument document = new PdfDocument(new PdfReader(resource)) ) {
            SignatureUtil signatureUtil = new SignatureUtil(document);
            List<String> names = signatureUtil.getSignatureNames();
            for (String name : names) {
                System.out.println("===== " + name + " =====");
                System.out.println("Signature covers whole document: " + signatureUtil.signatureCoversWholeDocument(name));
                System.out.println("Document revision: " + signatureUtil.getRevision(name) + " of " + signatureUtil.getTotalRevisions());
                PdfPKCS7 pkcs7 = signatureUtil.readSignatureData(name);
                System.out.println("Subject: " + CertificateInfo.getSubjectFields(pkcs7.getSigningCertificate()));
                System.out.println("Integrity check OK? " + pkcs7.verifySignatureIntegrityAndAuthenticity());
            }
            System.out.println();
        }
    }

    @Test
    public void testVerifyHideAndReplaceShadowFileSigned1() throws IOException, GeneralSecurityException {
        System.out.println("\n\nhide-and-replace-shadow-file-signed-1.pdf\n======");
        try (   InputStream resource = getClass().getResourceAsStream("hide-and-replace-shadow-file-signed-1.pdf");
                PdfDocument document = new PdfDocument(new PdfReader(resource)) ) {
            SignatureUtil signatureUtil = new SignatureUtil(document);
            List<String> names = signatureUtil.getSignatureNames();
            for (String name : names) {
                System.out.println("===== " + name + " =====");
                System.out.println("Signature covers whole document: " + signatureUtil.signatureCoversWholeDocument(name));
                System.out.println("Document revision: " + signatureUtil.getRevision(name) + " of " + signatureUtil.getTotalRevisions());
                PdfPKCS7 pkcs7 = signatureUtil.readSignatureData(name);
                System.out.println("Subject: " + CertificateInfo.getSubjectFields(pkcs7.getSigningCertificate()));
                System.out.println("Integrity check OK? " + pkcs7.verifySignatureIntegrityAndAuthenticity());
            }
            System.out.println();
        }
    }

    @Test
    public void testVerifyHideAndReplaceShadowFileSigned2() throws IOException, GeneralSecurityException {
        System.out.println("\n\nhide-and-replace-shadow-file-signed-2.pdf\n======");
        try (   InputStream resource = getClass().getResourceAsStream("hide-and-replace-shadow-file-signed-2.pdf");
                PdfDocument document = new PdfDocument(new PdfReader(resource)) ) {
            SignatureUtil signatureUtil = new SignatureUtil(document);
            List<String> names = signatureUtil.getSignatureNames();
            for (String name : names) {
                System.out.println("===== " + name + " =====");
                System.out.println("Signature covers whole document: " + signatureUtil.signatureCoversWholeDocument(name));
                System.out.println("Document revision: " + signatureUtil.getRevision(name) + " of " + signatureUtil.getTotalRevisions());
                PdfPKCS7 pkcs7 = signatureUtil.readSignatureData(name);
                System.out.println("Subject: " + CertificateInfo.getSubjectFields(pkcs7.getSigningCertificate()));
                System.out.println("Integrity check OK? " + pkcs7.verifySignatureIntegrityAndAuthenticity());
            }
            System.out.println();
        }
    }

    @Test
    public void testVerifyHideAndReplaceShadowFileSigned3() throws IOException, GeneralSecurityException {
        System.out.println("\n\nhide-and-replace-shadow-file-signed-3.pdf\n======");
        try (   InputStream resource = getClass().getResourceAsStream("hide-and-replace-shadow-file-signed-3.pdf");
                PdfDocument document = new PdfDocument(new PdfReader(resource)) ) {
            SignatureUtil signatureUtil = new SignatureUtil(document);
            List<String> names = signatureUtil.getSignatureNames();
            for (String name : names) {
                System.out.println("===== " + name + " =====");
                System.out.println("Signature covers whole document: " + signatureUtil.signatureCoversWholeDocument(name));
                System.out.println("Document revision: " + signatureUtil.getRevision(name) + " of " + signatureUtil.getTotalRevisions());
                PdfPKCS7 pkcs7 = signatureUtil.readSignatureData(name);
                System.out.println("Subject: " + CertificateInfo.getSubjectFields(pkcs7.getSigningCertificate()));
                System.out.println("Integrity check OK? " + pkcs7.verifySignatureIntegrityAndAuthenticity());
            }
            System.out.println();
        }
    }
}
