// $Id$
package mkl.testarea.itext7.content;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;


/**
 * @author mkl
 */
public class EnlargePagePart
{
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="http://stackoverflow.com/questions/35374110/how-do-i-use-itext-to-have-a-landscaped-pdf-on-half-of-a-a4-back-to-portrait-and">
     * How do i use iText to have a landscaped PDF on half of a A4 back to portrait and full size on A4
     * </a>
     * <p>
     * This sample shows how to rotate and enlarge the upper half of an A4 page to fit into a new A4 page.
     * </p>
     * 
     * @see mkl.testarea.itext5.content.EnlargePagePart
     */
    @Test
    public void testRotateAndZoomUpperHalfPage() throws IOException
    {
        try (   InputStream resource = getClass().getResourceAsStream("test.pdf");
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "test-upperHalf.pdf"))   )
        {
            PdfReader reader = new PdfReader(resource);
            PdfWriter writer = new PdfWriter(result);

            try (   PdfDocument srcDocument = new PdfDocument(reader);
                    PdfDocument pdfDocument = new PdfDocument(writer)   )
            {
                PdfPage page = srcDocument.getFirstPage();
                Rectangle pageSize = page.getPageSize();
                PdfFormXObject formXObject = page.copyAsFormXObject(pdfDocument);
                
                float sqrt2 = (float) Math.sqrt(2);
                page = pdfDocument.addNewPage(PageSize.A4);
                PdfCanvas pdfCanvas = new PdfCanvas(page);
                pdfCanvas.addXObjectWithTransformationMatrix(formXObject, 0, sqrt2, -sqrt2, 0, pageSize.getTop() * sqrt2, -pageSize.getLeft() * sqrt2);
           }
        }
    }
}
