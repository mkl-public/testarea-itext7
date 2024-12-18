package mkl.testarea.itext7.encryption;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.security.cert.Certificate;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.crypto.CryptoUtil;
import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

/**
 * @author mkl
 */
public class CreateCertEncyptedPdf {
    final static File RESULT_FOLDER = new File("target/test-outputs", "encryption");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void testCreateSimpleExample() throws IOException, GeneralSecurityException {
        
        try (
            PdfWriter pdfWriter = new PdfWriter(new File(RESULT_FOLDER, "simpleCertEncryptedExample.pdf").getAbsolutePath(),
                    new WriterProperties().setPublicKeyEncryption(new Certificate[] {CryptoUtil.readPublicCertificate(getClass().getResourceAsStream("test.cer"))}, new int[] {0}, EncryptionConstants.ENCRYPTION_AES_256));
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            Document document = new Document(pdfDocument);
        ) {
            document.add(new Paragraph("Test"));
        }
    }

}
