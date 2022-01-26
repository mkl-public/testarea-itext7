package mkl.testarea.itext7.signature;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Enumeration;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PdfSigner.CryptoStandard;
import com.itextpdf.signatures.PrivateKeySignature;

/**
 * @author mkl
 */
public class SignBrokenPdf {
    final static File RESULT_FOLDER = new File("target/test-outputs", "signature");

    final static String path = "keystores/demo-rsa2048.p12";
    final static char[] pass = "demo-rsa2048".toCharArray();
    static PrivateKey pk;
    static Certificate[] chain;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);

        KeyStore ks = KeyStore.getInstance("pkcs12", "SunJSSE");
        ks.load(new FileInputStream(path), pass);
        String alias = "";
        Enumeration<String> aliases = ks.aliases();
        while (alias.equals("demo") == false && aliases.hasMoreElements()) {
            alias = aliases.nextElement();
        }
        pk = (PrivateKey) ks.getKey(alias, pass);
        chain = ks.getCertificateChain(alias);
    }

    /**
     * <a href="https://stackoverflow.com/questions/70847879/itext-how-to-fix-cross-reference-table-for-signed-pdf">
     * iText how to fix cross reference table for signed pdf
     * </a>
     * <br/>
     * testBrokenXref.pdf, broken by setting one entry to "0000000000 65536 n"
     * <p>
     * As expected iText throws an exception when trying to sign the
     * given PDF with a broken cross reference entry in append mode.
     * </p>
     */
    @Test(expected = PdfException.class)
    public void testSignBrokenXref() throws IOException, GeneralSecurityException {
        try (   InputStream resource = getClass().getResourceAsStream("testBrokenXref.pdf");
                PdfReader pdfReader = new PdfReader(resource)) {
            pdfReader.setUnethicalReading(true);
            PdfSigner pdfSigner = new PdfSigner(pdfReader,
                    new ByteArrayOutputStream(),
                    new StampingProperties().useAppendMode());

            IExternalSignature pks = new PrivateKeySignature(pk, "SHA256", BouncyCastleProvider.PROVIDER_NAME);
            pdfSigner.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, CryptoStandard.CMS);
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/70847879/itext-how-to-fix-cross-reference-table-for-signed-pdf">
     * iText how to fix cross reference table for signed pdf
     * </a>
     * <br/>
     * testBrokenXref.pdf, broken by setting one entry to "0000000000 65536 n"
     * <p>
     * This test shows how to lead iText into believing the original
     * file was not broken, so it can be signed in append mode.
     * </p>
     */
    @Test
    public void testSignBrokenXrefForced() throws IOException, GeneralSecurityException {
        try (   InputStream resource = getClass().getResourceAsStream("testBrokenXref.pdf");
                PdfReader pdfReader = new PdfReader(resource) {
                    @Override
                    public boolean hasRebuiltXref() {
                        return false;
                    }

                    @Override
                    public boolean hasFixedXref() {
                        return false;
                    }
                }) {
            pdfReader.setUnethicalReading(true);
            PdfSigner pdfSigner = new PdfSigner(pdfReader,
                    new FileOutputStream(new File(RESULT_FOLDER, "testBrokenXref-signed.pdf")),
                    new StampingProperties().useAppendMode());

            IExternalSignature pks = new PrivateKeySignature(pk, "SHA256", BouncyCastleProvider.PROVIDER_NAME);
            pdfSigner.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, CryptoStandard.CMS);
        }
    }
}
