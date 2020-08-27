package mkl.testarea.itext7.signature;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;

/**
 * @author mkl
 */
public class ManipulateSignedPdf {
    final static File RESULT_FOLDER = new File("target/test-outputs", "signature");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * This test removes a page from a signed PDF and signs the result
     * non-incrementally.
     */
    @Test
    public void testRemovePageNonIncrementally() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("202007279900001S001.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfWriter pdfWriter = new PdfWriter(new File(RESULT_FOLDER, "202007279900001S001-pageRemovedNonIncr.pdf"));
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter)) {
            pdfDocument.removePage(2);
        }
    }

    /**
     * This test removes a page from a signed PDF and signs the result
     * incrementally.
     */
    @Test
    public void testRemovePageIncrementally() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("202007279900001S001.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfWriter pdfWriter = new PdfWriter(new File(RESULT_FOLDER, "202007279900001S001-pageRemovedIncr.pdf"));
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter, new StampingProperties().useAppendMode())) {
            pdfDocument.removePage(2);
        }
    }
}
