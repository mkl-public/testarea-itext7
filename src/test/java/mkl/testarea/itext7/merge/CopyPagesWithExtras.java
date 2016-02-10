package mkl.testarea.itext7.merge;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.forms.PdfPageFormCopier;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

/**
 * @author mkl
 */
public class CopyPagesWithExtras
{
    final static File RESULT_FOLDER = new File("target/test-outputs", "merge");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <p>
     * This test copies the single page from the output of the PdfClown AnnotationSample
     * into a new PDF.
     * </p>
     * <p>
     * As it turns out, the page copying mechanism currently fails to see
     * inherited properties. Thus, this copy operation takes place without
     * MediaBox and without Resources.
     * </p>
     * <p>
     * All the annotations, on the other hand, for which this test was written
     * in the first place, seem to have been copied correctly.
     * </p>
     */
    @Test
    public void testAnnotationSampleStandard() throws IOException
    {
        try (   InputStream resource = getClass().getResourceAsStream("AnnotationSample.Standard.pdf");
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "AnnotationSample.Standard-Copy.pdf"))   )
        {
            PdfReader reader = new PdfReader(resource);
            PdfWriter writer = new PdfWriter(result).setSmartMode(true);
            
            try (   PdfDocument source = new PdfDocument(reader);
                    PdfDocument target = new PdfDocument(writer)    )
            {
                source.copyPagesTo(1, source.getNumberOfPages(), target, new PdfPageFormCopier());
            }
        }
    }

}
