package mkl.testarea.itext7.signature;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.security.GeneralSecurityException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.signatures.ExternalBlankSignatureContainer;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSignatureAppearance.RenderingMode;
import com.itextpdf.signatures.PdfSigner;

/**
 * @author mkl
 */
public class SignHybridReference {
    final static File RESULT_FOLDER = new File("target/test-outputs", "signature");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/57273587/visible-signature-created-using-itext-7-now-shown-in-chrome">
     * Visible signature created using iText 7 now shown in chrome
     * </a>
     * <br/>
     * <a href="https://1drv.ms/b/s!AkROTDoCWFJnkd5W5P3MCbb8fwLASA?e=zsmks0">
     * WordSaveAsPdf_signed_doesnt_work_in_chrome.pdf
     * </a>, the original revision of which has been extracted as "WordSaveAsPdf.pdf"
     * <p>
     * Indeed, the result file does not show its signature appearance in Chrome.
     * One thing that is special about the result PDF is that the new revision
     * of the hybrid reference PDF contains an object stream with numerous
     * important objects, e.g. the updated page object and the new signature
     * field object
     * </p>
     * @see #testSignWordSaveAsPdfNoObjectStream()
     */
    @Test
    public void testSignWordSaveAsPdf() throws IOException, GeneralSecurityException {
        File destPath = new File(RESULT_FOLDER, "WordSaveAsPdf-Signed.pdf");
        try (   InputStream resource = getClass().getResourceAsStream("WordSaveAsPdf.pdf")  ) {
            PdfReader pdfReader = new PdfReader(resource);
            PdfSigner pdfSigner = new PdfSigner(pdfReader, new FileOutputStream(destPath), new StampingProperties().useAppendMode());

            pdfSigner.setFieldName("Signature1");

            PdfSignatureAppearance sigAppearance = pdfSigner.getSignatureAppearance();
            sigAppearance.setContact("ContactInfo");
            sigAppearance.setLocation("Location");
            sigAppearance.setPageNumber(1);
            sigAppearance.setPageRect(new Rectangle(60, 60, 360, 160));
            sigAppearance.setReason("SigningReason");
            sigAppearance.setLayer2Text("Lalelu");
            sigAppearance.setRenderingMode(RenderingMode.DESCRIPTION);
            sigAppearance.setSignatureCreator("Malik");

            int estimatedSize = 12000;
            pdfSigner.signExternalContainer(new ExternalBlankSignatureContainer(PdfName.Adobe_PPKLite, PdfName.Adbe_pkcs7_detached), estimatedSize);
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/57273587/visible-signature-created-using-itext-7-now-shown-in-chrome">
     * Visible signature created using iText 7 now shown in chrome
     * </a>
     * <br/>
     * <a href="https://1drv.ms/b/s!AkROTDoCWFJnkd5W5P3MCbb8fwLASA?e=zsmks0">
     * WordSaveAsPdf_signed_doesnt_work_in_chrome.pdf
     * </a>, the original revision of which has been extracted as "WordSaveAsPdf.pdf"
     * <p>
     * This test manipulates the PdfSigner class to deactivate full compression
     * in the PdfWriter. Thus, all the important objects are not in object
     * streams anymore. Et voila, the result file now does show its signature
     * appearance in Chrome!
     * </p>
     * @see #testSignWordSaveAsPdf()
     */
    @Test
    public void testSignWordSaveAsPdfNoObjectStream() throws IOException, GeneralSecurityException {
        File destPath = new File(RESULT_FOLDER, "WordSaveAsPdf-Signed-noObjectStream.pdf");
        try (   InputStream resource = getClass().getResourceAsStream("WordSaveAsPdf.pdf")  ) {
            PdfReader pdfReader = new PdfReader(resource);
            PdfSigner pdfSigner = new PdfSigner(pdfReader, new FileOutputStream(destPath), new StampingProperties().useAppendMode()) {
                @Override
                protected PdfDocument initDocument(PdfReader reader, PdfWriter writer, StampingProperties properties) {
                    try {
                        return super.initDocument(reader, writer, properties);
                    } finally {
                        try {
                            Field propertiesField = PdfWriter.class.getDeclaredField("properties");
                            propertiesField.setAccessible(true);
                            WriterProperties writerProperties = (WriterProperties) propertiesField.get(writer);
                            writerProperties.setFullCompressionMode(false);
                        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            };

            pdfSigner.setFieldName("Signature1");

            PdfSignatureAppearance sigAppearance = pdfSigner.getSignatureAppearance();
            sigAppearance.setContact("ContactInfo");
            sigAppearance.setLocation("Location");
            sigAppearance.setPageNumber(1);
            sigAppearance.setPageRect(new Rectangle(60, 60, 360, 160));
            sigAppearance.setReason("SigningReason");
            sigAppearance.setLayer2Text("Lalelu");
            sigAppearance.setRenderingMode(RenderingMode.DESCRIPTION);
            sigAppearance.setSignatureCreator("Malik");

            int estimatedSize = 12000;
            pdfSigner.signExternalContainer(new ExternalBlankSignatureContainer(PdfName.Adobe_PPKLite, PdfName.Adbe_pkcs7_detached), estimatedSize);
        }
    }

}
