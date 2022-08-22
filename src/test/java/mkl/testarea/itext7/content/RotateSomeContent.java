package mkl.testarea.itext7.content;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
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

    /**
     * <a href="https://stackoverflow.com/questions/73414465/rotate-an-itext7-table">
     * Rotate an Itext7 table
     * </a>
     * <p>
     * Setting the <code>RotationAngle</code> property seems not to work. But see
     * {@link #testForDropVid()}.
     * </p>
     */
    @Test
    public void testForDropVidProperty() throws FileNotFoundException {
        try (   PdfDocument pdf = new PdfDocument(new PdfWriter(new File(RESULT_FOLDER, "RotatedForDropVidProperty.pdf")))  ) {
            PdfPage page = pdf.addNewPage();
            PdfCanvas pdfCanvas = new PdfCanvas(page);

            Rectangle rectangle = new Rectangle(100, 100, 400, 700);
            try (   Canvas canvas = new Canvas(pdfCanvas, rectangle)    ) {
                Table table = new Table(5);
                table.addHeaderCell("DEBITO");
                table.addHeaderCell("INTERESSI DI MORA");
                table.addHeaderCell("ONERI DI RISCOSSIONE");
                table.addHeaderCell("SPESE DI NOTIFICA\nE ACCESSORI");
                table.addHeaderCell("SPESE ESECUTIVE");
                table.addCell("3.304,24");
                table.addCell("0,00");
                table.addCell("183,55");
                table.addCell("8,75");
                table.addCell("0,00");
                table.setRotationAngle(-Math.PI/2);

                canvas.add(table);
            }
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/73414465/rotate-an-itext7-table">
     * Rotate an Itext7 table
     * </a>
     * <p>
     * Setting the <code>RotationAngle</code> property seems not to work, see
     * {@link #testForDropVidProperty()}, but manually rotating the canvas does.
     * </p>
     */
    @Test
    public void testForDropVid() throws FileNotFoundException {
        try (   PdfDocument pdf = new PdfDocument(new PdfWriter(new File(RESULT_FOLDER, "RotatedForDropVid.pdf")))  ) {
            PdfPage page = pdf.addNewPage();
            PdfCanvas pdfCanvas = new PdfCanvas(page);

            // Rectangle for the table in upright page coordinates
            Rectangle rectangle = new Rectangle(100, 100, 400, 700);
            // show rectangle area
            pdfCanvas.saveState();
            pdfCanvas.setFillColor(new DeviceRgb(255, 255, 128));
            pdfCanvas.rectangle(rectangle);
            pdfCanvas.fill();
            pdfCanvas.restoreState();

            // apply a translation and a rotation so that the table will be rotated
            // and the origin will be in the lower left corner of the rectangle
            AffineTransform transform = AffineTransform.getTranslateInstance(rectangle.getLeft(), rectangle.getTop());
            transform.rotate(-Math.PI/2);
            pdfCanvas.concatMatrix(transform);

            Rectangle rotatedRectangle = new Rectangle(0, 0, rectangle.getHeight(), rectangle.getWidth());

            try (   Canvas canvas = new Canvas(pdfCanvas, rotatedRectangle)    ) {
                Table table = new Table(5);
                table.addHeaderCell("DEBITO");
                table.addHeaderCell("INTERESSI DI MORA");
                table.addHeaderCell("ONERI DI RISCOSSIONE");
                table.addHeaderCell("SPESE DI NOTIFICA\nE ACCESSORI");
                table.addHeaderCell("SPESE ESECUTIVE");
                table.addCell("3.304,24");
                table.addCell("0,00");
                table.addCell("183,55");
                table.addCell("8,75");
                table.addCell("0,00");

                canvas.add(table);
            }
        }
    }
}
