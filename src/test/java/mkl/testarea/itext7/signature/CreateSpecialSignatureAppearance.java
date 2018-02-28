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
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Enumeration;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.CertificateInfo;
import com.itextpdf.signatures.IExternalDigest;
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

    /**
     * <a href="https://stackoverflow.com/questions/49008108/font-size-for-name-and-description-pdf-digital-signature">
     * Font size for Name and Description PDF Digital signature
     * </a>
     * <p>
     * This test shows how to create a custom layer 2 appearance for a signature,
     * in this case a variation of the default using customized font sizes.
     * </p>
     */
    @Test
    public void testCustomizeLayer2TextStyle() throws IOException, GeneralSecurityException {
        String reason = "Just another illusionary reason";
        String loc = "Right around the corner";
        String digestAlgorithm = "SHA512";
        CryptoStandard subfilter = CryptoStandard.CMS;

        try (   InputStream inStream = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf");
                OutputStream pdfos = new FileOutputStream(new File(RESULT_FOLDER, "test-signed-styled-text.pdf"))) {
            PdfReader reader = new PdfReader(inStream);
            PdfSigner signer = new PdfSigner(reader, pdfos, false);

            int noOfPages = signer.getDocument().getNumberOfPages();
            PdfSignatureAppearance appearance = signer.getSignatureAppearance().setReason(reason).setLocation(loc)
                    .setReuseAppearance(false);
            Rectangle rect = new Rectangle(250, 100, 200, 80);
// instead of this
//            appearance.setRenderingMode(RenderingMode.NAME_AND_DESCRIPTION);
//            appearance.setLayer2FontSize(6.0f);
            appearance.setPageRect(rect).setPageNumber(noOfPages);
            signer.setFieldName("sign");

// create the appearance yourself using code borrowed from iText's default for NAME_AND_DESCRIPTION
            PdfFormXObject layer2 = appearance.getLayer2();
            PdfCanvas canvas = new PdfCanvas(layer2, signer.getDocument());

            float MARGIN = 2;
            PdfFont font = PdfFontFactory.createFont();

            String name = null;
            CertificateInfo.X500Name x500name = CertificateInfo.getSubjectFields((X509Certificate)chain[0]);
            if (x500name != null) {
                name = x500name.getField("CN");
                if (name == null)
                    name = x500name.getField("E");
            }
            if (name == null)
                name = "";

            Rectangle dataRect = new Rectangle(rect.getWidth() / 2 + MARGIN / 2, MARGIN, rect.getWidth() / 2 - MARGIN, rect.getHeight() - 2 * MARGIN);
            Rectangle signatureRect = new Rectangle(MARGIN, MARGIN, rect.getWidth() / 2 - 2 * MARGIN, rect.getHeight() - 2 * MARGIN);

// using different, customized font sizes 
            try (Canvas layoutCanvas = new Canvas(canvas, signer.getDocument(), signatureRect);) {
                Paragraph paragraph = new Paragraph(name).setFont(font).setMargin(0).setMultipliedLeading(0.9f).setFontSize(20);
                layoutCanvas.add(paragraph);
            }

            try (Canvas layoutCanvas = new Canvas(canvas, signer.getDocument(), dataRect);) {
                Paragraph paragraph = new Paragraph().setFont(font).setMargin(0).setMultipliedLeading(0.9f);
                paragraph.add(new Text("Digitally signed by ").setFontSize(6));
                paragraph.add(new Text(name + '\n').setFontSize(9));
                paragraph.add(new Text("Date: " + new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z").format(signer.getSignDate().getTime()) + '\n').setFontSize(6));
                paragraph.add(new Text("Reason: " + appearance.getReason() + '\n').setFontSize(6));
                paragraph.add(new Text("Location: " + appearance.getLocation()).setFontSize(6));
                layoutCanvas.add(paragraph);
            }
// that's it!

            // Creating the signature
            IExternalSignature pks = new PrivateKeySignature(pk, digestAlgorithm, "BC");
            IExternalDigest digest = new BouncyCastleDigest();
            signer.signDetached(digest, pks, chain, null, null, null, 0, subfilter);
        }        
    }
}
