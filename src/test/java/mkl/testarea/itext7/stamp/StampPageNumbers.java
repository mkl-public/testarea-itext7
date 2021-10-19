package mkl.testarea.itext7.stamp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

/**
 * @author mkl
 */
public class StampPageNumbers
{
    final static File RESULT_FOLDER = new File("target/test-outputs", "stamp");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="http://stackoverflow.com/questions/39941721/itext7-java-adding-text-issue">
     * iText7 Java adding text issue
     * </a>
     * <br/>
     * <a href="https://dl.dropboxusercontent.com/u/75924564/Simulated_doc.pdf">
     * Simulated_doc.pdf
     * </a>
     * <p>
     * Due to micro-optimizations, iText indeed adds all page numbers to the
     * same single copy of the original imported page. This can be fixed in
     * {@link #numberingPage(PdfPage, String)} by using a different, not so
     * micro-optimized constructor of {@link PdfCanvas}.
     * </p>
     */
    @Test
    public void testCopyPagesAndStampLikeUser6839234() throws IOException
    {
        final String[] NUM4SAMPLE = {"02A", "03A", "03B", "03C", "04A", "08A"};

        try (   InputStream resource = getClass().getResourceAsStream("Simulated_doc.pdf");
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "Simulated_doc-numbered.pdf")) )
        {
            manipulatePdf(resource, result, NUM4SAMPLE);
        }
    }

    private void manipulatePdf(InputStream src, OutputStream dest, String[] numbering4what) throws IOException
    {
        // Initialize PDF document
        PdfDocument pdfDocToRead = new PdfDocument(new PdfReader(src));
        PdfDocument pdfDocToWrite = new PdfDocument(new PdfWriter(dest));

        for (String s : numbering4what)
        {
            println(s);
        }

        String number = null;
        PdfPage tempPage = null;
        for (int i = 0; i < numbering4what.length; i++)
        {
            pdfDocToRead.copyPagesTo(1, 2, pdfDocToWrite);
            number = numbering4what[i];
            println(number);
            tempPage = pdfDocToWrite.getPage(2 * (i + 1) - 1);

            numberingPage(tempPage, number);

            println("pdfDocToWrite.numberOfPages : " + pdfDocToWrite.getNumberOfPages());
        }

        pdfDocToRead.close();
        pdfDocToWrite.close();

        println("\nNumber added!");
    }

    private void numberingPage(PdfPage pdfPage, String number) throws IOException
    {
        final double XCOOR = 230;
        final double YCOOR = 795;//755

        println(pdfPage);
        // Originally, the canvas was created using
        // PdfCanvas canvas = new PdfCanvas(pdfPage);
        // This constructor turns out to be over-optimized and effectively
        // always adds content to the same single copy of the original page
        // content stream.
        // The replacement below circumvents this over-optimization and so
        // resolves the issue.
        PdfCanvas canvas = new PdfCanvas(pdfPage.newContentStreamAfter(), pdfPage.getResources(), pdfPage.getDocument());
        canvas.beginText().setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 22).moveText(XCOOR, YCOOR)
                .showText(number).endText();

        println("number: " + number);

    }

    private void println(Object obj)
    {
        System.out.println(obj);
    }
}
