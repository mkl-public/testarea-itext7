package mkl.testarea.itext7.meta;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

/**
 * @author mkl
 */
public class BreakByHandling {
    final static File RESULT_FOLDER = new File("target/test-outputs", "meta");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/46479449/itext-7-0-4-0-pdfwriter-produces-corrupted-pdf-for-certain-pdf-file-inputs">
     * iText 7.0.4.0 - PdfWriter produces corrupted PDF for certain PDF file inputs
     * </a>
     * <br/>
     * <a href="https://drive.google.com/open?id=0B3NPOZswWocQV09KMW5fbFVyUm8">
     * sample1_input.pdf
     * </a>
     * <p>
     * The issue can be reproduced in iText 7.0.4 but not anymore in 7.0.5
     * SNAPSHOT, and there indeed is a commit described as "Fix bugs in pages
     * tree rebuilding" in a block described as "handle mix of PdfPage and
     * PdfPages". The PDF in question does have a page tree node with mixed
     * child node types, Page and Pages, and the latter subtree even does
     * not contain any Page descendants.  
     * </p>
     */
    @Test
    public void testBreakByHandlingLikeJamesK() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("sample1_input.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfWriter pdfWriter = new PdfWriter(new File(RESULT_FOLDER, "sample1_handled.pdf"));
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter)) {
        }
    }
}
