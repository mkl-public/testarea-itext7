package mkl.testarea.itext7.content;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;

/**
 * @author mkl
 */
public class AddImage {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/71488649/itext-add-image-to-an-existing-pdf">
     * iText - add image to an existing PDF
     * </a>
     * <br/>
     * <a href="https://ipupload.com/tP6/step1.pdf">
     * step1.pdf
     * </a>
     * <p>
     * Indeed, this creates an invalid PDF as the changes to the resources,
     * the added image resource, are not stored in the result file. The cause
     * is that the addition to the resources only marks the resources and the
     * specific resource type dictionary as changed (both of which are direct
     * objects in the case at hand) and the contents array already is indirect,
     * so changes to it don't mark the page changed, either.
     * </p>
     * @see #testLikeKalpitPlusPage()
     */
    @Test
    public void testLikeKalpit() throws IOException {
        File destFile = new File(RESULT_FOLDER, "step1-test1-kd2.pdf");
        try (   InputStream resource = getClass().getResourceAsStream("step1.pdf");
                InputStream imageResource = getClass().getResourceAsStream("Graph.png") ) {
            PdfDocument pdf = new PdfDocument(new PdfReader(resource), new PdfWriter(destFile), new StampingProperties().useAppendMode());

            Document document = new Document(pdf);

            ImageData data = ImageDataFactory.create(StreamUtil.inputStreamToArray(imageResource));

            Image image = new Image(data);
            document.add(image);

            document.close();
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/71488649/itext-add-image-to-an-existing-pdf">
     * iText - add image to an existing PDF
     * </a>
     * <br/>
     * <a href="https://ipupload.com/tP6/step1.pdf">
     * step1.pdf
     * </a>
     * <p>
     * In contrast to {@link #testLikeKalpit()} we here explicitly mark the
     * page object in question as changed. As a consequence, the changes in
     * the resources also are stored and all is ok.
     * </p>
     * @see #testLikeKalpit()
     */
    @Test
    public void testLikeKalpitPlusPage() throws IOException {
        File destFile = new File(RESULT_FOLDER, "step1-test1-kd2-withPage.pdf");
        try (   InputStream resource = getClass().getResourceAsStream("step1.pdf");
                InputStream imageResource = getClass().getResourceAsStream("Graph.png") ) {
            PdfDocument pdf = new PdfDocument(new PdfReader(resource), new PdfWriter(destFile), new StampingProperties().useAppendMode());

            Document document = new Document(pdf);

            ImageData data = ImageDataFactory.create(StreamUtil.inputStreamToArray(imageResource));

            Image image = new Image(data);
            document.add(image);
            pdf.getFirstPage().setModified();

            document.close();
        }
    }
}
