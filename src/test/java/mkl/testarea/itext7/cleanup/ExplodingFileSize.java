package mkl.testarea.itext7.cleanup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.pdfcleanup.PdfCleanUpLocation;
import com.itextpdf.pdfcleanup.PdfCleanUpTool;

/**
 * @author mkl
 */
public class ExplodingFileSize {
    final static File RESULT_FOLDER = new File("target/test-outputs", "cleanup");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/73306767/itext-7-for-net-new-file-is-10-times-bigger-then-before-using-pdfcleanup">
     * iText 7 for .NET - New File is 10 times bigger then before using PdfCleanUp
     * </a>
     * <br/>
     * <a href="https://1drv.ms/b/s!Aj7x-HrNBqDw7EbEXNxcOq3kxTz7">
     * export.pdf
     * </a>
     * <p>
     * Indeed, the file size indeed explodes...
     * </p>
     */
    @Test
    public void testLikeMH14() throws IOException {
        try (
            InputStream resource = getClass().getResourceAsStream("export.pdf");
            PdfDocument pdfDoc = new PdfDocument(new PdfReader(resource), new PdfWriter(new File(RESULT_FOLDER, "export-redacted.pdf")))
        ) {
            DeviceRgb white = new DeviceRgb(255, 255, 255);
            DeviceRgb grey = new DeviceRgb(220, 220, 220);

            PdfCleanUpLocation pdfcleanhead = new PdfCleanUpLocation(1, new Rectangle(40, 774, 500, 27), white);
            PdfCleanUpLocation pdfcleancontent = new PdfCleanUpLocation(1, new Rectangle(190, 682, 100, 15), grey);

            PdfCleanUpTool cleaner = new PdfCleanUpTool(pdfDoc);
            cleaner.addCleanupLocation(pdfcleanhead);
            cleaner.addCleanupLocation(pdfcleancontent);

            cleaner.cleanUp();
            
        }
    }

    @Test
    public void test() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PdfDocument pdf = new PdfDocument(new PdfWriter(baos))) {
            PdfPage page = pdf.addNewPage();
            Rectangle cropBox = page.getCropBox();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas.setLineWidth(5);
            canvas.moveTo(cropBox.getLeft(), cropBox.getBottom());
            canvas.lineTo(cropBox.getRight() + 50*cropBox.getWidth() + Math.PI, cropBox.getTop() + 50 * cropBox.getHeight() - Math.E);
            canvas.stroke();
            canvas.setLineWidth(50);
            canvas.moveTo(cropBox.getRight() + 50 * (cropBox.getWidth() + Math.E), cropBox.getBottom() - 50 * (cropBox.getHeight() + Math.PI));
            canvas.lineTo(cropBox.getLeft(), cropBox.getTop());
            canvas.stroke();

            canvas.concatMatrix(new AffineTransform(0.02834933, 0, 0, 0.02834754, 0, 842));
            canvas.setFillColor(new DeviceRgb(255, 0, 0));
            canvas.setLineCapStyle(2);
            canvas.setMiterLimit(10);
            canvas.setLineJoinStyle(0);
            canvas.setLineDash(new float[0], 0);
            canvas.rectangle(2756, -8737, 17066, -12506);
            canvas.fill();
            canvas.setStrokeColor(new DeviceGray());
            canvas.moveTo(2634, -14990);
            canvas.lineTo(2755, -14990);
            canvas.stroke();
        }
        final byte[] source = baos.toByteArray();
        Files.write(new File(RESULT_FOLDER, "test.pdf").toPath(), source);

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(source)), new PdfWriter(new File(RESULT_FOLDER, "test-redacted.pdf")))) {
            DeviceRgb white = new DeviceRgb(255, 255, 255);
            DeviceRgb grey = new DeviceRgb(220, 220, 220);

            PdfCleanUpLocation pdfcleanhead = new PdfCleanUpLocation(1, new Rectangle(40, 774, 500, 27), white);
            PdfCleanUpLocation pdfcleancontent = new PdfCleanUpLocation(1, new Rectangle(190, 682, 100, 15), grey);

            PdfCleanUpTool cleaner = new PdfCleanUpTool(pdfDoc);
            cleaner.addCleanupLocation(pdfcleanhead);
            cleaner.addCleanupLocation(pdfcleancontent);

            cleaner.cleanUp();
        }
    }
}
