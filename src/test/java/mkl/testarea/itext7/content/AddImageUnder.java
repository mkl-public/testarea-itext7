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
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

/**
 * @author mkl
 */
public class AddImageUnder {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/53231903/how-to-bring-image-to-the-frontof-the-text-image-or-send-the-image-to-the-back">
     * How to bring image to the front(of the text/image) or send the image to the back((of the text/image)) in IText7 using java?
     * </a>
     * <p>
     * Indeed, Adobe Reader complains about an error on the page.
     * The cause is the use of a throw-away resources object. It
     * causes the image resource to not end up in the PDF at all.
     * </p>
     * @see #testAddLikeKishorePenmetsaFixed()
     */
    @Test
    public void testAddLikeKishorePenmetsa() throws IOException {
        try (   InputStream pdfResource = getClass().getResourceAsStream("test.pdf");
                InputStream imgResource = getClass().getResourceAsStream("Oskar.jpg");) {
            PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdfResource),
                    new PdfWriter(new File(RESULT_FOLDER, "test-image-added-like-kishore-penmetsa.pdf")));
            ImageData img = ImageDataFactory.create(StreamUtil.inputStreamToArray(imgResource));
            PdfCanvas under = new PdfCanvas(pdfDoc.getFirstPage().newContentStreamBefore(), new PdfResources(), pdfDoc);
            under.addImage(img, 100, 0f, 0f, 100, 100, 300, false);
            under.saveState();
            pdfDoc.close();
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/53231903/how-to-bring-image-to-the-frontof-the-text-image-or-send-the-image-to-the-back">
     * How to bring image to the front(of the text/image) or send the image to the back((of the text/image)) in IText7 using java?
     * </a>
     * <p>
     * Using the page resources instead of a throw-away resources object
     * fixes the issue. Furthermore, the dangling <code>saveState</code>
     * instruction has been removed.
     * </p>
     * @see #testAddLikeKishorePenmetsa()
     */
    @Test
    public void testAddLikeKishorePenmetsaFixed() throws IOException {
        try (   InputStream pdfResource = getClass().getResourceAsStream("test.pdf");
                InputStream imgResource = getClass().getResourceAsStream("Oskar.jpg");) {
            PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdfResource),
                    new PdfWriter(new File(RESULT_FOLDER, "test-image-added-like-kishore-penmetsa-fixed.pdf")));
            ImageData img = ImageDataFactory.create(StreamUtil.inputStreamToArray(imgResource));
            PdfCanvas under = new PdfCanvas(pdfDoc.getFirstPage().newContentStreamBefore(), pdfDoc.getFirstPage().getResources(), pdfDoc);
            under.addImage(img, 100, 0f, 0f, 100, 100, 300, false);
            pdfDoc.close();
        }
    }

}
