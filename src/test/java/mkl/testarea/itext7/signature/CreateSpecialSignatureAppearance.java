package mkl.testarea.itext7.signature;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Enumeration;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PdfSigner.CryptoStandard;
import com.itextpdf.signatures.PrivateKeySignature;

/**
 * @author mkl
 */
public class CreateSpecialSignatureAppearance {
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
        while (alias.equals("demo") == false && aliases.hasMoreElements())
        {
            alias = aliases.nextElement();
        }
        pk = (PrivateKey) ks.getKey(alias, pass);
        chain = ks.getCertificateChain(alias);
    }

    /**
     * <a href="https://stackoverflow.com/questions/48467637/signature-appearance-font-color-in-layer2-itext-7">
     * Signature appearance font color in layer2 (itext 7)
     * </a>
     * <p>
     * Indeed, the option to transport color via the font in iText 5
     * seems not to have been taken into account when porting the
     * PdfSignatureAppearance class to iText 7.
     * </p>
     * <p>
     * Of course there always is the option of creating any layer
     * manually. But if one does not want to do so, one can tweak
     * the layer content after making iText create the appearances;
     * the latter requires reflection, though.
     * </p>
     */
    @Test
    public void testColorizeLayer2Text() throws IOException, GeneralSecurityException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        boolean isAppendMode = false;
        int certificationLevel = PdfSigner.NOT_CERTIFIED;
        String reason = "Just another illusionary reason";
        String location = "Right around the corner";
        boolean setReuseAppearance = false;
        Rectangle rectangleForNewField = new Rectangle(100, 500, 200, 50);
        Float fontSize = null;
        String name = "TheTestSignature";
        String digestAlgorithm = "SHA512";
        CryptoStandard subfilter = CryptoStandard.CMS;

        try (   InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf");
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "test-signed-colored-text.pdf"))) {
            PdfReader reader = new PdfReader(resource);
            PdfSigner signer = new PdfSigner(reader, result, isAppendMode);

            signer.setCertificationLevel(certificationLevel);

            // Creating the appearance
            PdfSignatureAppearance appearance = signer.getSignatureAppearance()
                    .setReason(reason)
                    .setLocation(location)
                    .setReuseAppearance(setReuseAppearance);

            if (rectangleForNewField != null) {
                appearance.setPageRect(rectangleForNewField);
            }
            if (fontSize != null) {
                appearance.setLayer2FontSize((float) fontSize);
            }

            signer.setFieldName(name);

            Method getAppearanceMethod = PdfSignatureAppearance.class.getDeclaredMethod("getAppearance");
            getAppearanceMethod.setAccessible(true);
            getAppearanceMethod.invoke(appearance);

            PdfFormXObject layer2 = appearance.getLayer2();
            PdfStream layer2Stream = layer2.getPdfObject();
            byte[] layer2Bytes = layer2Stream.getBytes();
            layer2Stream.setData("1 0 0 rg\n".getBytes());
            layer2Stream.setData(layer2Bytes, true);

            // Creating the signature
            IExternalSignature pks = new PrivateKeySignature(pk, digestAlgorithm, BouncyCastleProvider.PROVIDER_NAME);
            signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, subfilter);
        }
    }

}
