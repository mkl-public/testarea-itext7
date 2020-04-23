package mkl.testarea.itext7.content;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

/**
 * @author mkl
 */
public class RotatePageXObject {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/61350200/how-set-rotation-angle-a-page-in-itext-7">
     * How set rotation angle a page in itext 7
     * </a>
     * <p>
     * This test shows how to add a page at an arbitrary rotation onto another one.
     * </p>
     */
    @Test
    public void testAddPage25Degree() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("li.persia.pdf");
                PdfDocument srcDoc = new PdfDocument(new PdfReader(resource));
                PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new File(RESULT_FOLDER, "li.persia-25Degree.pdf").getAbsolutePath(), new WriterProperties().setFullCompressionMode(true)))) {
            PdfCanvas content = new PdfCanvas(pdfDoc.addNewPage());
            PageSize pageSize = pdfDoc.getDefaultPageSize();
            PdfFormXObject page = srcDoc.getPage(1).copyAsFormXObject(pdfDoc);
            AffineTransform transform = AffineTransform.getRotateInstance(25 * Math.PI / 180, (pageSize.getLeft() + pageSize.getRight())/2, (pageSize.getBottom() + pageSize.getTop())/2);
            content.concatMatrix(transform);
            content.addXObject(page, 0, 0);
        }
    }

}
