// $Id$
package mkl.testarea.itext7.merge;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;

/**
 * @author mkl
 */
public class DuplicatePages
{
    final static File RESULT_FOLDER = new File("target/test-outputs", "merge");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * Copying pages multiple times into the new PDF works all right.
     */
    @Test
    public void testMergeDuplicatePages() throws IOException
    {
        String ifs2 = "src\\test\\resources\\mkl\\testarea\\itext7\\merge\\pro-linq-in-c-2010.pdf";
        File result = new File(RESULT_FOLDER, "pro-linq-in-c-2010-twice.pdf");
        
        try (   PdfReader reader = new PdfReader(ifs2);
                PdfDocument sourceDocument = new PdfDocument(reader);
                PdfWriter writer = new PdfWriter(new FileOutputStream(result));
                PdfDocument pdfDocument = new PdfDocument(writer)   )
        {
            PdfMerger pdfMerger = new PdfMerger(pdfDocument);
            pdfMerger.addPages(sourceDocument, 1, sourceDocument.getNumberOfPages());
            pdfMerger.addPages(sourceDocument, 1, sourceDocument.getNumberOfPages());
            pdfMerger.merge();
        }
    }

}
