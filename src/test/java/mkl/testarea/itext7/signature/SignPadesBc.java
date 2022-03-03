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

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.IExternalSignatureContainer;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.TSAClientBouncyCastle;

/**
 * @author mkl
 */
public class SignPadesBc {
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
     * <a href="https://stackoverflow.com/questions/71225696/base64-digest-pfxpkcs12-etsi-cades-detached-signature-pades-ltv">
     * Base64 digest + PFX(PKCS12) -> ETSI.CAdES.detached signature -> PAdES LTV
     * </a>
     * <p>
     * This test generates a simple PAdES BASELINE-T signature.
     * </p>
     */
    @Test
    public void testSignPadesBaselineT() throws IOException, GeneralSecurityException, OperatorException {
        try (   InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf")  ) {
            PdfSigner pdfSigner = new PdfSigner(new PdfReader(resource),
                    new FileOutputStream(new File(RESULT_FOLDER, "PadesBc.pdf")),
                    new StampingProperties());
            IExternalSignatureContainer container = new PadesSignatureContainerBc(new X509CertificateHolder(chain[0].getEncoded()),
                    new JcaContentSignerBuilder("SHA512withRSA").build(pk),
                    new TSAClientBouncyCastle("http://timestamp.server/rfc3161endpoint"));
            pdfSigner.signExternalContainer(container, 8192);
        }
    }
}
