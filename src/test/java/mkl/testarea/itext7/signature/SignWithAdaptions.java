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

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
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
public class SignWithAdaptions {
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
     * <a href="https://stackoverflow.com/questions/64678798/itext-sign-pdf-with-custom-signed-by-name">
     * iText Sign PDF with custom “Signed by” name
     * </a>
     * <p>
     * Adobe Acrobat uses the Name value in its signature panel
     * if it cannot properly parse the signature container or if
     * automatic validation during PDF opening is deactivated.
     * Unfortunately not, though, if automatic validation is
     * activated and the signature container can be parsed which
     * is the most common case.
     * </p>
     */
    @Test
    public void testSignWithNameInEvent() throws IOException, GeneralSecurityException {
        try (   InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf")  ) {
            PdfSigner pdfSigner = new PdfSigner(new PdfReader(resource),
                    new FileOutputStream(new File(RESULT_FOLDER, "test-signedWithName.pdf")),
                    new StampingProperties());
            pdfSigner.setSignatureEvent(new PdfSigner.ISignatureEvent() {
                @Override
                public void getSignatureDictionary(PdfSignature sig) {
                    sig.setName("A Custom Signer");
                }
            });

            IExternalSignature pks = new PrivateKeySignature(pk, "SHA256", BouncyCastleProvider.PROVIDER_NAME);
            pdfSigner.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, CryptoStandard.CMS);
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/69625369/signature-field-remain-unsigned-after-the-signing">
     * Signature field remain unsigned after the signing
     * </a>
     * <br/>
     * <a href="https://drive.google.com/file/d/1yJZsnAbD2yytHqp-y0Xg7b6hPCnj0_-Q/view?usp=sharing">
     * test.pdf
     * </a> as "Fda1571.pdf"
     * <p>
     * This test illustrates how to sign a signature visibly in a form field
     * that is set hidden.
     * </p>
     */
    @Test
    public void testFda1571() throws IOException, GeneralSecurityException {
        try (   InputStream resource = getClass().getResourceAsStream("Fda1571.pdf");
                PdfReader pdfReader = new PdfReader(resource)) {
            pdfReader.setUnethicalReading(true);
            PdfSigner pdfSigner = new PdfSigner(pdfReader,
                    new FileOutputStream(new File(RESULT_FOLDER, "Fda1571-signedWithoutHidden.pdf")),
                    new StampingProperties());
            pdfSigner.setFieldName("signature1");
            PdfAcroForm.getAcroForm(pdfSigner.getDocument(), false)
                .getField("signature1")
                .setVisibility(PdfFormField.VISIBLE);

            IExternalSignature pks = new PrivateKeySignature(pk, "SHA256", BouncyCastleProvider.PROVIDER_NAME);
            pdfSigner.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, CryptoStandard.CMS);
        }
    }
}
