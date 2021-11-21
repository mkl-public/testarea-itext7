package mkl.testarea.itext7.signature;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import com.itextpdf.forms.fields.PdfSignatureFormField;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSignatureAppearance.RenderingMode;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PdfSigner.CryptoStandard;
import com.itextpdf.signatures.PrivateKeySignature;

/**
 * @author mkl
 */
public class CreateSignatureAppearance {
    final static File RESULT_FOLDER = new File("target/test-outputs", "signature");

    final static String path = "keystores/johndoe.p12";
    final static char[] pass = "johndoe".toCharArray();
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
        while (alias.equals("johndoe") == false && aliases.hasMoreElements()) {
            alias = aliases.nextElement();
        }
        pk = (PrivateKey) ks.getKey(alias, pass);
        chain = ks.getCertificateChain(alias);
    }

    @Test
    public void testModeDescription() throws IOException, GeneralSecurityException {
        try (   InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "test-DESCRIPTION.pdf")) ) {
            PdfSigner pdfSigner = new PdfSigner(pdfReader, result, new StampingProperties());

            PdfSignatureAppearance appearance = pdfSigner.getSignatureAppearance();
            appearance.setPageRect(new Rectangle(100, 500, 300, 100));
            appearance.setPageNumber(1);
            appearance.setRenderingMode(RenderingMode.DESCRIPTION);
            appearance.setReason("Specimen");
            appearance.setLocation("Boston");

            IExternalSignature pks = new PrivateKeySignature(pk, "SHA256", BouncyCastleProvider.PROVIDER_NAME);
            pdfSigner.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, CryptoStandard.CMS);


//            appearance.setCertificate(chain[0]);
//            pdfSigner.signExternalContainer(new ExternalBlankSignatureContainer(new PdfName("MKLx_BLANK_SIGNER"), new PdfName("MKLx_DESCRIPTION")), 1024);
        }
    }

    @Test
    public void testModeGraphic() throws IOException, GeneralSecurityException {
        try (   InputStream imageResource = getClass().getResourceAsStream("iText badge.png");
                InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "test-GRAPHIC.pdf")) ) {
            ImageData data = ImageDataFactory.create(StreamUtil.inputStreamToArray(imageResource));

            PdfSigner pdfSigner = new PdfSigner(pdfReader, result, new StampingProperties());

            PdfSignatureAppearance appearance = pdfSigner.getSignatureAppearance();
            appearance.setPageRect(new Rectangle(100, 500, 300, 100));
            appearance.setPageNumber(1);
            appearance.setRenderingMode(RenderingMode.GRAPHIC);
            appearance.setSignatureGraphic(data);
            appearance.setReason("Specimen");
            appearance.setLocation("Boston");

            IExternalSignature pks = new PrivateKeySignature(pk, "SHA256", BouncyCastleProvider.PROVIDER_NAME);
            pdfSigner.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, CryptoStandard.CMS);
        }
    }

    @Test
    public void testModeGraphicAndDescription() throws IOException, GeneralSecurityException {
        try (   InputStream imageResource = getClass().getResourceAsStream("johnDoe.png");
                InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "test-GRAPHIC_AND_DESCRIPTION.pdf")) ) {
            ImageData data = ImageDataFactory.create(StreamUtil.inputStreamToArray(imageResource));

            PdfSigner pdfSigner = new PdfSigner(pdfReader, result, new StampingProperties());

            PdfSignatureAppearance appearance = pdfSigner.getSignatureAppearance();
            appearance.setPageRect(new Rectangle(100, 500, 300, 100));
            appearance.setPageNumber(1);
            appearance.setRenderingMode(RenderingMode.GRAPHIC_AND_DESCRIPTION);
            appearance.setSignatureGraphic(data);
            appearance.setReason("Specimen");
            appearance.setLocation("Boston");

            IExternalSignature pks = new PrivateKeySignature(pk, "SHA256", BouncyCastleProvider.PROVIDER_NAME);
            pdfSigner.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, CryptoStandard.CMS);
        }
    }

    @Test
    public void testModeNameAndDescription() throws IOException, GeneralSecurityException {
        try (   InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "test-NAME_AND_DESCRIPTION.pdf")) ) {
            PdfSigner pdfSigner = new PdfSigner(pdfReader, result, new StampingProperties());

            PdfSignatureAppearance appearance = pdfSigner.getSignatureAppearance();
            appearance.setPageRect(new Rectangle(100, 500, 300, 100));
            appearance.setPageNumber(1);
            appearance.setRenderingMode(RenderingMode.NAME_AND_DESCRIPTION);
            appearance.setReason("Specimen");
            appearance.setLocation("Boston");
            appearance.setCertificate(chain[0]);

            IExternalSignature pks = new PrivateKeySignature(pk, "SHA256", BouncyCastleProvider.PROVIDER_NAME);
            pdfSigner.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, CryptoStandard.CMS);
        }
    }

    @Test
    public void testReuseAppearance() throws IOException, GeneralSecurityException {
        File emptySignatureFile = createEmptySignatureField();

        try (   PdfReader pdfReader = new PdfReader(emptySignatureFile);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "emptySignatureField-signed.pdf")) ) {
            PdfSigner pdfSigner = new PdfSigner(pdfReader, result, new StampingProperties());
            pdfSigner.setFieldName("Signature");

            PdfSignatureAppearance appearance = pdfSigner.getSignatureAppearance();
            appearance.setRenderingMode(RenderingMode.NAME_AND_DESCRIPTION);
            appearance.setReason("Specimen");
            appearance.setLocation("Boston");
            appearance.setCertificate(chain[0]);
            appearance.setReuseAppearance(true);

            IExternalSignature pks = new PrivateKeySignature(pk, "SHA256", BouncyCastleProvider.PROVIDER_NAME);
            pdfSigner.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, CryptoStandard.CMS);
        }
    }

    File createEmptySignatureField() throws IOException {
        File emptySignatureFile = new File(RESULT_FOLDER, "emptySignatureField.pdf");
        try (   PdfDocument pdfDocument = new PdfDocument(new PdfWriter(emptySignatureFile))) {
            PdfSignatureFormField field = PdfFormField.createSignature(pdfDocument, new Rectangle(100, 600, 300, 100));
            field.setFieldName("Signature");
            createAppearance(field, pdfDocument);
            PdfAcroForm.getAcroForm(pdfDocument, true).addField(field, pdfDocument.addNewPage());
        }
        return emptySignatureFile;
    }
    

    void createAppearance(PdfSignatureFormField field, PdfDocument pdfDocument) throws IOException {
        PdfWidgetAnnotation widget = field.getWidgets().get(0);
        Rectangle rectangle = field.getWidgets().get(0).getRectangle().toRectangle();
        rectangle = new Rectangle(rectangle.getWidth(), rectangle.getHeight()); // necessary because of iText bug
        PdfFormXObject xObject = new PdfFormXObject(rectangle);
        xObject.makeIndirect(pdfDocument);
        PdfCanvas canvas = new PdfCanvas(xObject, pdfDocument);
        canvas.setExtGState(new PdfExtGState().setFillOpacity(.5f));
        try (   InputStream imageResource = getClass().getResourceAsStream("Binary - Light Gray.png")    ) {
            ImageData data = ImageDataFactory.create(StreamUtil.inputStreamToArray(imageResource));
            canvas.addImageFittedIntoRectangle(data, rectangle, false);
        }
        widget.setNormalAppearance(xObject.getPdfObject());
    }

    @Test
    public void testSetImage() throws IOException, GeneralSecurityException {
        try (   InputStream imageResource = getClass().getResourceAsStream("Binary - Orange.png");
                InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "test-setImage.pdf")) ) {
            ImageData data = ImageDataFactory.create(StreamUtil.inputStreamToArray(imageResource));
            PdfSigner pdfSigner = new PdfSigner(pdfReader, result, new StampingProperties());

            PdfSignatureAppearance appearance = pdfSigner.getSignatureAppearance();
            appearance.setPageRect(new Rectangle(100, 500, 300, 100));
            appearance.setPageNumber(1);
            appearance.setRenderingMode(RenderingMode.NAME_AND_DESCRIPTION);
            appearance.setReason("Specimen");
            appearance.setLocation("Boston");
            appearance.setCertificate(chain[0]);
            appearance.setImage(data);
            appearance.setImageScale(-1);

            IExternalSignature pks = new PrivateKeySignature(pk, "SHA256", BouncyCastleProvider.PROVIDER_NAME);
            pdfSigner.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, CryptoStandard.CMS);
        }
    }

    @Test
    public void testSetCaptions() throws IOException, GeneralSecurityException {
        try (   InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "test-setCaptions.pdf")) ) {
            PdfSigner pdfSigner = new PdfSigner(pdfReader, result, new StampingProperties());

            PdfSignatureAppearance appearance = pdfSigner.getSignatureAppearance();
            appearance.setPageRect(new Rectangle(100, 500, 300, 100));
            appearance.setPageNumber(1);
            appearance.setRenderingMode(RenderingMode.DESCRIPTION);
            appearance.setReasonCaption("Objective: ");
            appearance.setReason("Specimen");
            appearance.setLocationCaption("Whereabouts: ");
            appearance.setLocation("Boston");

            IExternalSignature pks = new PrivateKeySignature(pk, "SHA256", BouncyCastleProvider.PROVIDER_NAME);
            pdfSigner.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, CryptoStandard.CMS);
        }
    }

    @Test
    public void testSetFontStyle() throws IOException, GeneralSecurityException {
        try (   InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "test-SetFontStyle.pdf")) ) {
            PdfSigner pdfSigner = new PdfSigner(pdfReader, result, new StampingProperties());

            PdfSignatureAppearance appearance = pdfSigner.getSignatureAppearance();
            appearance.setPageRect(new Rectangle(100, 500, 300, 100));
            appearance.setPageNumber(1);
            appearance.setRenderingMode(RenderingMode.NAME_AND_DESCRIPTION);
            appearance.setReason("Specimen");
            appearance.setLocation("Boston");
            appearance.setCertificate(chain[0]);
            appearance.setLayer2Font(PdfFontFactory.createFont(StandardFonts.COURIER));
            appearance.setLayer2FontColor(new DeviceRgb(0xF9, 0x9D, 0x25));
            appearance.setLayer2FontSize(10);

            IExternalSignature pks = new PrivateKeySignature(pk, "SHA256", BouncyCastleProvider.PROVIDER_NAME);
            pdfSigner.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, CryptoStandard.CMS);
        }
    }

    @Test
    public void testSetDescriptionText() throws IOException, GeneralSecurityException {
        try (   InputStream imageResource = getClass().getResourceAsStream("iText logo.png");
                InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "test-SetDescriptionText.pdf")) ) {
            ImageData data = ImageDataFactory.create(StreamUtil.inputStreamToArray(imageResource));

            PdfSigner pdfSigner = new PdfSigner(pdfReader, result, new StampingProperties());

            PdfSignatureAppearance appearance = pdfSigner.getSignatureAppearance();
            appearance.setPageRect(new Rectangle(100, 500, 300, 100));
            appearance.setPageNumber(1);
            appearance.setRenderingMode(RenderingMode.GRAPHIC_AND_DESCRIPTION);
            appearance.setSignatureGraphic(data);
            String restriction = "The qualified electronic signature at hand is restricted to present offers, invoices or credit notes to customers according to EU REGULATION No 910/2014 (23 July 2014) and German VAT law (§14 UStG).";
            appearance.setReason(restriction);
            appearance.setLayer2Text(restriction);

            IExternalSignature pks = new PrivateKeySignature(pk, "SHA256", BouncyCastleProvider.PROVIDER_NAME);
            pdfSigner.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, CryptoStandard.CMS);
        }
    }

    @Test
    public void testCustomLayer0() throws IOException, GeneralSecurityException {
        try (   InputStream imageResource = getClass().getResourceAsStream("johnDoe.png");
                InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "test-CustomLayer0.pdf")) ) {
            ImageData data = ImageDataFactory.create(StreamUtil.inputStreamToArray(imageResource));

            PdfSigner pdfSigner = new PdfSigner(pdfReader, result, new StampingProperties());

            PdfSignatureAppearance appearance = pdfSigner.getSignatureAppearance();
            appearance.setPageRect(new Rectangle(100, 500, 300, 100));
            appearance.setPageNumber(1);
            appearance.setRenderingMode(RenderingMode.GRAPHIC_AND_DESCRIPTION);
            appearance.setSignatureGraphic(data);
            appearance.setReason("Specimen");
            appearance.setLocation("Boston");

            PdfFormXObject layer0 = appearance.getLayer0();
            Rectangle rectangle = layer0.getBBox().toRectangle();
            PdfCanvas canvas = new PdfCanvas(layer0, pdfSigner.getDocument());
            canvas.setStrokeColor(new DeviceRgb(0xF9, 0x9D, 0x25)).setLineWidth(2);
            for (int i = (int)(rectangle.getLeft() - rectangle.getHeight()); i < rectangle.getRight(); i += 5)
                canvas.moveTo(i, rectangle.getBottom()).lineTo(i + rectangle.getHeight(), rectangle.getTop());
            canvas.stroke();

            IExternalSignature pks = new PrivateKeySignature(pk, "SHA256", BouncyCastleProvider.PROVIDER_NAME);
            pdfSigner.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, CryptoStandard.CMS);
        }
    }

    @Test
    public void testCustomLayer2() throws IOException, GeneralSecurityException {
        try (   InputStream badgeResource = getClass().getResourceAsStream("iText badge.png");
                InputStream signResource = getClass().getResourceAsStream("johnDoe.png");
                InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "test-CustomLayer2.pdf")) ) {
            ImageData badge = ImageDataFactory.create(StreamUtil.inputStreamToArray(badgeResource));
            ImageData sign = ImageDataFactory.create(StreamUtil.inputStreamToArray(signResource));

            PdfSigner pdfSigner = new PdfSigner(pdfReader, result, new StampingProperties());

            PdfSignatureAppearance appearance = pdfSigner.getSignatureAppearance();
            appearance.setPageRect(new Rectangle(100, 500, 300, 100));
            appearance.setPageNumber(1);

            PdfFormXObject layer2 = appearance.getLayer2();
            Rectangle rectangle = layer2.getBBox().toRectangle();
            PdfCanvas canvas = new PdfCanvas(layer2, pdfSigner.getDocument());

            float xCenter = rectangle.getLeft() + rectangle.getWidth() / 2;
            float yCenter = rectangle.getBottom() + rectangle.getHeight() / 2;

            float badgeWidth = rectangle.getHeight() - 20;
            float badgeHeight = badgeWidth * badge.getHeight() / badge.getWidth();

            canvas.setLineWidth(20)
                  .setStrokeColorRgb(.9f, .1f, .1f)
                  .moveTo(rectangle.getLeft(), rectangle.getBottom())
                  .lineTo(rectangle.getRight(), rectangle.getTop())
                  .moveTo(xCenter + rectangle.getHeight(), yCenter - rectangle.getWidth())
                  .lineTo(xCenter - rectangle.getHeight(), yCenter + rectangle.getWidth())
                  .stroke();

            sign.setTransparency(new int[] {0, 0});
            canvas.addImageFittedIntoRectangle(sign, new Rectangle(0, yCenter, badgeWidth * sign.getWidth() / sign.getHeight() / 2, badgeWidth / 2), false);

            canvas.concatMatrix(AffineTransform.getRotateInstance(Math.atan2(rectangle.getHeight(), rectangle.getWidth()), xCenter, yCenter));
            canvas.addImageFittedIntoRectangle(badge, new Rectangle(xCenter - badgeWidth / 2, yCenter - badgeHeight + badgeWidth / 2, badgeWidth, badgeHeight), false);

            IExternalSignature pks = new PrivateKeySignature(pk, "SHA256", BouncyCastleProvider.PROVIDER_NAME);
            pdfSigner.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, CryptoStandard.CMS);
        }
    }

    @Test
    public void testCustomLayers() throws IOException, GeneralSecurityException {
        try (   InputStream badgeResource = getClass().getResourceAsStream("iText badge.png");
                InputStream signResource = getClass().getResourceAsStream("johnDoe.png");
                InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "test-CustomLayers.pdf")) ) {
            ImageData badge = ImageDataFactory.create(StreamUtil.inputStreamToArray(badgeResource));
            ImageData sign = ImageDataFactory.create(StreamUtil.inputStreamToArray(signResource));

            PdfSigner pdfSigner = new PdfSigner(pdfReader, result, new StampingProperties());

            PdfSignatureAppearance appearance = pdfSigner.getSignatureAppearance();
            appearance.setPageRect(new Rectangle(100, 500, 300, 100));
            appearance.setPageNumber(1);

            PdfFormXObject layer0 = appearance.getLayer0();
            Rectangle rectangle = layer0.getBBox().toRectangle();
            PdfCanvas canvas = new PdfCanvas(layer0, pdfSigner.getDocument());
            canvas.setStrokeColor(new DeviceRgb(0xF9, 0x9D, 0x25)).setLineWidth(2);
            for (int i = (int)(rectangle.getLeft() - rectangle.getHeight()); i < rectangle.getRight(); i += 5)
                canvas.moveTo(i, rectangle.getBottom()).lineTo(i + rectangle.getHeight(), rectangle.getTop());
            canvas.stroke();

            PdfFormXObject layer2 = appearance.getLayer2();
            rectangle = layer2.getBBox().toRectangle();
            canvas = new PdfCanvas(layer2, pdfSigner.getDocument());

            float xCenter = rectangle.getLeft() + rectangle.getWidth() / 2;
            float yCenter = rectangle.getBottom() + rectangle.getHeight() / 2;

            float badgeWidth = rectangle.getHeight() - 20;
            float badgeHeight = badgeWidth * badge.getHeight() / badge.getWidth();

            canvas.setLineWidth(20)
                  .setStrokeColorRgb(.9f, .1f, .1f)
                  .moveTo(rectangle.getLeft(), rectangle.getBottom())
                  .lineTo(rectangle.getRight(), rectangle.getTop())
                  .moveTo(xCenter + rectangle.getHeight(), yCenter - rectangle.getWidth())
                  .lineTo(xCenter - rectangle.getHeight(), yCenter + rectangle.getWidth())
                  .stroke();

            sign.setTransparency(new int[] {0, 0});
            canvas.addImageFittedIntoRectangle(sign, new Rectangle(0, yCenter, badgeWidth * sign.getWidth() / sign.getHeight() / 2, badgeWidth / 2), false);

            canvas.concatMatrix(AffineTransform.getRotateInstance(Math.atan2(rectangle.getHeight(), rectangle.getWidth()), xCenter, yCenter));
            canvas.addImageFittedIntoRectangle(badge, new Rectangle(xCenter - badgeWidth / 2, yCenter - badgeHeight + badgeWidth / 2, badgeWidth, badgeHeight), false);

            IExternalSignature pks = new PrivateKeySignature(pk, "SHA256", BouncyCastleProvider.PROVIDER_NAME);
            pdfSigner.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, CryptoStandard.CMS);
        }
    }

    @Test
    public void testCustomLayer2OnReusedAppearance() throws IOException, GeneralSecurityException {
        File emptySignatureFile = createEmptySignatureField();

        try (   InputStream badgeResource = getClass().getResourceAsStream("iText badge.png");
                InputStream signResource = getClass().getResourceAsStream("johnDoe.png");
                PdfReader pdfReader = new PdfReader(emptySignatureFile);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "test-CustomLayer2OnReusedAppearance.pdf")) ) {
            ImageData badge = ImageDataFactory.create(StreamUtil.inputStreamToArray(badgeResource));
            ImageData sign = ImageDataFactory.create(StreamUtil.inputStreamToArray(signResource));

            PdfSigner pdfSigner = new PdfSigner(pdfReader, result, new StampingProperties());
            pdfSigner.setFieldName("Signature");

            PdfSignatureAppearance appearance = pdfSigner.getSignatureAppearance();
            appearance.setReuseAppearance(true);

            PdfFormXObject layer2 = appearance.getLayer2();
            Rectangle rectangle = layer2.getBBox().toRectangle();
            PdfCanvas canvas = new PdfCanvas(layer2, pdfSigner.getDocument());

            float xCenter = rectangle.getLeft() + rectangle.getWidth() / 2;
            float yCenter = rectangle.getBottom() + rectangle.getHeight() / 2;

            float badgeWidth = rectangle.getHeight() - 20;
            float badgeHeight = badgeWidth * badge.getHeight() / badge.getWidth();

            canvas.setLineWidth(20)
                  .setStrokeColorRgb(.9f, .1f, .1f)
                  .moveTo(rectangle.getLeft(), rectangle.getBottom())
                  .lineTo(rectangle.getRight(), rectangle.getTop())
                  .moveTo(xCenter + rectangle.getHeight(), yCenter - rectangle.getWidth())
                  .lineTo(xCenter - rectangle.getHeight(), yCenter + rectangle.getWidth())
                  .stroke();

            sign.setTransparency(new int[] {0, 0});
            canvas.addImageFittedIntoRectangle(sign, new Rectangle(0, yCenter, badgeWidth * sign.getWidth() / sign.getHeight() / 2, badgeWidth / 2), false);

            canvas.concatMatrix(AffineTransform.getRotateInstance(Math.atan2(rectangle.getHeight(), rectangle.getWidth()), xCenter, yCenter));
            canvas.addImageFittedIntoRectangle(badge, new Rectangle(xCenter - badgeWidth / 2, yCenter - badgeHeight + badgeWidth / 2, badgeWidth, badgeHeight), false);

            IExternalSignature pks = new PrivateKeySignature(pk, "SHA256", BouncyCastleProvider.PROVIDER_NAME);
            pdfSigner.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, CryptoStandard.CMS);
        }
    }

    @Test
    public void testMachineReadables() throws IOException, GeneralSecurityException {
        try (   InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "test-MachineReadables.pdf")) ) {
            PdfSigner pdfSigner = new PdfSigner(pdfReader, result, new StampingProperties());

            PdfSignatureAppearance appearance = pdfSigner.getSignatureAppearance();
            appearance.setContact("Test content of Contact field");
            appearance.setReason("Test content of Reason field");
            appearance.setLocation("Test content of Location field");
            appearance.setSignatureCreator("Test content of Signature Creator field");

            IExternalSignature pks = new PrivateKeySignature(pk, "SHA256", BouncyCastleProvider.PROVIDER_NAME);
            pdfSigner.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, CryptoStandard.CMS);
        }
    }
}
