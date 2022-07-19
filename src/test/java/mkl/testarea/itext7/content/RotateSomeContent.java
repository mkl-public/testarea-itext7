package mkl.testarea.itext7.content;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;

/**
 * @author mkl
 */
public class RotateSomeContent {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/73032682/how-to-rotate-a-rectangle-having-text-as-content-to-some-angle-let-say-90-in-co">
     * How to rotate a rectangle having text as content to some angle let say 90' in counterclockwise direction with itext 7.1.0?
     * </a>
     * <p>
     * This test shows one way to generate rotated content on a page.
     * </p>
     */
    @Test
    public void testForAnkushGupta() throws IOException {
        try (   PdfDocument pdf = new PdfDocument(new PdfWriter(new File(RESULT_FOLDER, "RotatedForAnkushGupta.pdf")))  ) {
            PdfPage page = pdf.addNewPage();
            PdfCanvas pdfCanvas = new PdfCanvas(page);

            Rectangle rect1 = new Rectangle(183, 488, 180, 32);

            AffineTransform transform = AffineTransform.getRotateInstance(Math.PI/2, rect1.getX(), rect1.getY());
            pdfCanvas.concatMatrix(transform);

            pdfCanvas.rectangle(rect1);
            pdfCanvas.stroke();

            try (   Canvas canvas = new Canvas(pdfCanvas, rect1)    ) {
                Text title = new Text("Thbvhs ybhsvb");
                Paragraph p = new Paragraph().add(title);
                canvas.add(p);
            }
        }
        
    }

}
