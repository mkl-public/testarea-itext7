package mkl.testarea.itext7.content;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;

/**
 * @author mkl
 */
public class RenderBeforeAndBehind {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/71126038/inserting-an-image-after-text">
     * Inserting an image after text
     * </a>
     * <p>
     * This test corresponds to the OP's code and adds the image in front.
     * </p>
     */
    @Test
    public void testRenderBefore() throws IOException {
        try (
            InputStream resource = getClass().getResourceAsStream("test.pdf");
            InputStream imageResource = getClass().getResourceAsStream("Oskar.jpg");
            PdfDocument pdfDocument = new PdfDocument(new PdfReader(resource), new PdfWriter(new File(RESULT_FOLDER, "NewContentBefore.pdf")));
            Document document = new Document(pdfDocument);
        ) {
            Image image = new Image(ImageDataFactory.create(StreamUtil.inputStreamToArray(imageResource)));
            image.scaleAbsolute(256, 192);
            image.setFixedPosition(1, 150, 700);
            image.setRotationAngle(- Math.PI / 2);
            document.add(image);
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/71126038/inserting-an-image-after-text">
     * Inserting an image after text
     * </a>
     * <p>
     * This test shows how to change the OP's code to add the image in the back.
     * </p>
     */
    @Test
    public void testRenderBehind() throws IOException {
        try (
            InputStream resource = getClass().getResourceAsStream("test.pdf");
            InputStream imageResource = getClass().getResourceAsStream("Oskar.jpg");
            PdfDocument pdfDocument = new PdfDocument(new PdfReader(resource), new PdfWriter(new File(RESULT_FOLDER, "NewContentBehind.pdf")));
        ) {
            Image image = new Image(ImageDataFactory.create(StreamUtil.inputStreamToArray(imageResource)));
            image.scaleAbsolute(256, 192);
            image.setFixedPosition(1, 150, 700);
            image.setRotationAngle(- Math.PI / 2);

            PdfPage pdfPage = pdfDocument.getFirstPage();
            PdfCanvas pdfCanvas = new PdfCanvas(pdfPage.newContentStreamBefore(), pdfPage.getResources(), pdfDocument);
            try (   Canvas canvas = new Canvas(pdfCanvas, pdfPage.getCropBox()) ) {
                canvas.add(image);
            }
        }
    }
}
