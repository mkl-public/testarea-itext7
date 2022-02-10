package mkl.testarea.itext7.content;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

/**
 * @author mkl
 */
public class LargeCoordinateValues {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://community.adobe.com/t5/acrobat-sdk-discussions/number-out-of-range-error-when-gathering-content-from-pdf-content-stream/m-p/12739946">
     * Number out of range error when gathering content from PDF content stream
     * </a>
     * <p>
     * Using coordinate values in the 10000s nothing happens yet, but see
     * {@link #testCoordinateAt100000()}.
     * </p>
     */
    @Test
    public void testCoordinateAt10000() throws IOException {
        try (   PdfWriter pdfWriter = new PdfWriter(new File(RESULT_FOLDER, "CoordinateAt10000.pdf"));
                PdfDocument pdfDocument = new PdfDocument(pdfWriter)    ) {
            Rectangle box = new Rectangle(10000, 10000, 500, 500);
            PdfPage pdfPage = pdfDocument.addNewPage(new PageSize(box));
            PdfCanvas pdfCanvas = new PdfCanvas(pdfPage);
            pdfCanvas.setFillColor(ColorConstants.BLUE)
                     .setStrokeColor(ColorConstants.RED)
                     .rectangle(box)
                     .fillStroke();
        }
    }

    /**
     * <a href="https://community.adobe.com/t5/acrobat-sdk-discussions/number-out-of-range-error-when-gathering-content-from-pdf-content-stream/m-p/12739946">
     * Number out of range error when gathering content from PDF content stream
     * </a>
     * <p>
     * Indeed, using coordinates in the 100000s, Adobe Reader rejects this,
     * but see {@link #testCoordinateAt100000SmallBox()}.
     * </p>
     */
    @Test
    public void testCoordinateAt100000() throws IOException {
        try (   PdfWriter pdfWriter = new PdfWriter(new File(RESULT_FOLDER, "CoordinateAt100000.pdf"));
                PdfDocument pdfDocument = new PdfDocument(pdfWriter)    ) {
            Rectangle box = new Rectangle(99750, 99750, 500, 500);
            PdfPage pdfPage = pdfDocument.addNewPage(new PageSize(box));
            PdfCanvas pdfCanvas = new PdfCanvas(pdfPage);
            pdfCanvas.setStrokeColor(ColorConstants.RED)
                     .setLineWidth(10)
                     .moveTo(100000, 100000)
                     .lineTo(-100000, -100000)
                     .stroke();
        }
    }

    /**
     * <a href="https://community.adobe.com/t5/acrobat-sdk-discussions/number-out-of-range-error-when-gathering-content-from-pdf-content-stream/m-p/12739946">
     * Number out of range error when gathering content from PDF content stream
     * </a>
     * <p>
     * In spite of using coordinates in the 100000s nothing happens.
     * Apparently the issue is not with the coordinates in the content
     * stream but with the media box coordinates (which here are small).
     * </p>
     */
    @Test
    public void testCoordinateAt100000SmallBox() throws IOException {
        try (   PdfWriter pdfWriter = new PdfWriter(new File(RESULT_FOLDER, "CoordinateAt100000SmallBox.pdf"));
                PdfDocument pdfDocument = new PdfDocument(pdfWriter)    ) {
            Rectangle box = new Rectangle(-250, -250, 500, 500);
            PdfPage pdfPage = pdfDocument.addNewPage(new PageSize(box));
            PdfCanvas pdfCanvas = new PdfCanvas(pdfPage);
            pdfCanvas.setStrokeColor(ColorConstants.RED)
                     .setLineWidth(10)
                     .moveTo(100000, 100000)
                     .lineTo(-100000, -100000)
                     .stroke();
        }
    }
}
