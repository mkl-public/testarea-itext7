// $Id$
package mkl.testarea.itext7.annotate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;

/**
 * @author mkl
 *
 */
public class MarkAnnotationReadOnly
{
    final static File RESULT_FOLDER = new File("target/test-outputs", "annotate");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="http://stackoverflow.com/questions/37275267/how-to-make-pdf-annotation-as-read-only-using-itext">
     * how to make pdf annotation as read only using itext?
     * </a>
     * <br/>
     * test-annotated.pdf <i>simple PDF with sticky note</i>
     * 
     * <p>
     * This test shows how to set the read-only flags of all annotations of a document.
     * </p>
     */
    @Test
    public void test() throws IOException
    {
        try (   InputStream resourceStream = getClass().getResourceAsStream("test-annotated.pdf");
                OutputStream outputStream = new FileOutputStream(new File(RESULT_FOLDER, "test-annotated-ro.pdf"));
                PdfReader pdfReader = new PdfReader(resourceStream);
                PdfWriter pdfWriter = new PdfWriter(outputStream);
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter) )
        {
            for (int page = 1; page <= pdfDocument.getNumberOfPages(); page++)
            {
                PdfPage pdfPage = pdfDocument.getPage(page);
                for (PdfAnnotation pdfAnnotation : pdfPage.getAnnotations())
                {
                    pdfAnnotation.setFlag(PdfAnnotation.READ_ONLY);
                }
            }
        }
    }
}
