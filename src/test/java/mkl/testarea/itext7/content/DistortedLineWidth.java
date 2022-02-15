package mkl.testarea.itext7.content;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

/**
 * @author mkl
 */
public class DistortedLineWidth {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://community.adobe.com/t5/acrobat-sdk-discussions/recalculate-line-width-with-ctm/m-p/12753505">
     * Recalculate line width with CTM
     * </a>
     * <p>
     * This test illustrates what it means that there is no line width in device space
     * - it draws a curve (well, a pair of curves) without changing the width, merely
     * distorted by transformation.
     * </p>
     */
    @Test
    public void testDistortedCurve() throws IOException {
        try (   PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new File(RESULT_FOLDER, "DistortedCurve.pdf"))) ) {
            PdfCanvas pdfCanvas = new PdfCanvas(pdfDocument.addNewPage(new PageSize(300, 100)));
            pdfCanvas.concatMatrix(5, 0, 0, 1, 25, 25);
            pdfCanvas.arc(0, 0, 50, 50, 0, 180);
            pdfCanvas.stroke();
        }
    }
}
