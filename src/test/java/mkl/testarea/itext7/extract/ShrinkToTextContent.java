package mkl.testarea.itext7.extract;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.TextMarginFinder;

/**
 * @author mkl
 */
public class ShrinkToTextContent {
    final static File RESULT_FOLDER = new File("target/test-outputs", "extract");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/53777698/itext-7-c-sharp-how-to-clip-an-existing-pdf">
     * itext 7 c# how to clip an existing pdf
     * </a>
     * <p>
     * This test shows how to restrict pages to their (text)
     * content. Pages restricted like this can be imported
     * onto other document pages in a more naturally looking
     * way.
     * </p>
     */
    @Test
    public void testRestrictTestGustavoPiucco() throws IOException {
        try (   InputStream pdfResource = getClass().getResourceAsStream("testGustavoPiucco.pdf");
                OutputStream resultStream = new FileOutputStream(new File(RESULT_FOLDER, "testGustavoPiucco-restricted.pdf"));
                PdfDocument document = new PdfDocument(new PdfReader(pdfResource), new PdfWriter(resultStream))
                ) {
            for (int i = 1; i <= document.getNumberOfPages(); i++) {
                PdfPage page = document.getPage(i);
                restrictToText(page);
            }
        }
    }

    /** @see #testRestrictTestGustavoPiucco() */
    PdfPage restrictToText(PdfPage page) {
        TextMarginFinder finder = new TextMarginFinder();
        new PdfCanvasProcessor(finder).processPageContent(page);
        Rectangle textRect = finder.getTextRectangle();
        page.setMediaBox(textRect);
        page.setCropBox(textRect);
        return page;
    }
}
