package mkl.testarea.itext7.signature;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Security;

import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.x509.util.StreamParsingException;
import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.CrlClientOnline;
import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.IOcspClient;
import com.itextpdf.signatures.OcspClientBouncyCastle;

/**
 * @author mkl
 */
public class MakeLtvEnabled {
    final static File RESULT_FOLDER = new File("target/test-outputs", "signature");

    @BeforeClass
    public static void setUp() throws Exception {
        RESULT_FOLDER.mkdirs();

        BouncyCastleProvider bcp = new BouncyCastleProvider();
        Security.insertProviderAt(bcp, 1);
    }

    /**
     * <a href="https://stackoverflow.com/questions/51639464/itext7-ltvverification-addverification-not-enabling-ltv">
     * iText7 LtvVerification.addVerification not enabling LTV
     * </a>
     * <br/>
     * <a href="https://stackoverflow.com/questions/51370965/how-can-i-add-pades-ltv-using-itext">
     * how can I add PAdES-LTV using itext
     * </a>
     * <br/>
     * <a href="https://drive.google.com/file/d/18xiNeLZG0jcz3HGxa5qAug3szpRuJqvw/view?usp=sharing">
     * sign_without_LTV.pdf
     * </a>
     * <p>
     * This tests the {@link AdobeLtvEnabling} utility class. The current Adobe Reader accepts
     * the output as LTV enabled after trusting the root certificate of the signer chain.
     * </p>
     */
    @Test
    public void testLtvEnableSignWithoutLtv() throws IOException, GeneralSecurityException, StreamParsingException, OCSPException, OperatorException {
        try (   InputStream resource = getClass().getResourceAsStream("sign_without_LTV.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfWriter pdfWriter = new PdfWriter(new File(RESULT_FOLDER, "sign_without_LTV-LtvEnabled.pdf"));
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter, new StampingProperties().preserveEncryption().useAppendMode())) {
            AdobeLtvEnabling adobeLtvEnabling = new AdobeLtvEnabling(pdfDocument);
            IOcspClient ocsp = new OcspClientBouncyCastle(null);
            ICrlClient crl = new CrlClientOnline();
            adobeLtvEnabling.enable(ocsp, crl);
        }
    }
}
