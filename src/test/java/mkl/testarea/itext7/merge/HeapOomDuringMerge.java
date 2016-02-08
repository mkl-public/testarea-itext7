package mkl.testarea.itext7.merge;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

/**
 * @author mkl
 */
public class HeapOomDuringMerge
{
    final static File RESULT_FOLDER = new File("target/test-outputs", "merge");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="http://stackoverflow.com/questions/35231801/pdf-merge-using-itext-throws-oom-error">
     * PDF merge using iText throws OOM error
     * </a>
     * <br/>
     * <a href="https://saibababojja.files.wordpress.com/2012/03/pro-linq-in-c-2010.pdf">
     * pro-linq-in-c-2010.pdf
     * </a>
     * <p>
     * Indeed, quite some memory is required to do this merger, but on Java 8 I actually
     * needed 725 MB (-Xmx725m) with iText 5 and 500 MB with iText 7 for 
     * <code>smart = false</code>. For <code>smart = true</code>, on the other hand, I
     * needed 700 MB with iText 5 while even 2 GB did not suffice for iText 7 RC1. 
     * </p>
     * 
     * @see mkl.testarea.itext5.merge.HeapOomDuringMerge
     */
    @Test
    public void testMergeLikeLe_Master() throws IOException
    {
//        String ifs2 = "C:\\Downloads\\02.pdf";
//        String result = "C:\\Merge\\final.pdf";
        String ifs2 = "src\\test\\resources\\mkl\\testarea\\itext7\\merge\\pro-linq-in-c-2010.pdf";
        String result = new File(RESULT_FOLDER, "finalLe_Master.pdf").toString();
        String[] stArray = new String[10]; 
        for(int i = 0; i<10; i++){
            stArray[i]=ifs2;
        }

        mergeFiles(stArray,result, false);
    }

    public static void mergeFiles(String[] files, String result, boolean smart) throws IOException
    {
        PdfWriter writer = new PdfWriter(new FileOutputStream(result));
        writer.setSmartCopyMode(smart);
        PdfDocument pdfDocument = new PdfDocument(writer);

        for (int i = 0; i < files.length; i++)
        {
            System.out.println(i);
            PdfReader reader = new PdfReader(files[i]);
            PdfDocument sourceDocument = new PdfDocument(reader);
            sourceDocument.copyPages(1, sourceDocument.getNumberOfPages(), pdfDocument);
            sourceDocument.close();
            reader.close();
        }
        
        pdfDocument.close();
    }
}
