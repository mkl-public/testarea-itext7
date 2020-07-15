package mkl.testarea.itext7.signature;

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

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSignature;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PdfSigner.CryptoStandard;
import com.itextpdf.signatures.PrivateKeySignature;

/**
 * @author mkl
 */
public class SignWithNulls {
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
     * This test tries to create a signature without reason and location entries.
     * Unfortunately it fails, iText signing always tries to set the reason and
     * location.
     */
    @Test
    public void testNullReasonAndLocation() throws IOException, GeneralSecurityException {
        try (   InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf")  ) {
            PdfSigner pdfSigner = new PdfSigner(new PdfReader(resource),
                    new FileOutputStream(new File(RESULT_FOLDER, "NullReasonAndLocation.pdf")),
                    new StampingProperties());
            pdfSigner.getSignatureAppearance()
                     .setReason(null)
                     .setLocation(null);

            IExternalSignature pks = new PrivateKeySignature(pk, "SHA224", BouncyCastleProvider.PROVIDER_NAME);
            pdfSigner.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, CryptoStandard.CADES);
        }
    }

    /**
     * This test tries to create a signature without reason and location entries.
     * It does so not by setting them to null in the signature appearance, though,
     * but instead by registering a ISignatureEvent that removes the entries from
     * the finished signature dictionary right before signing. This works.
     */
    @Test
    public void testNullReasonAndLocationInEvent() throws IOException, GeneralSecurityException {
        try (   InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf")  ) {
            PdfSigner pdfSigner = new PdfSigner(new PdfReader(resource),
                    new FileOutputStream(new File(RESULT_FOLDER, "NullReasonAndLocationEvent.pdf")),
                    new StampingProperties());
            pdfSigner.setSignatureEvent(new PdfSigner.ISignatureEvent() {
                @Override
                public void getSignatureDictionary(PdfSignature sig) {
                    sig.getPdfObject().remove(PdfName.Reason);
                    sig.getPdfObject().remove(PdfName.Location);
                }
            });

            IExternalSignature pks = new PrivateKeySignature(pk, "SHA224", BouncyCastleProvider.PROVIDER_NAME);
            pdfSigner.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, CryptoStandard.CADES);
        }
    }
}
