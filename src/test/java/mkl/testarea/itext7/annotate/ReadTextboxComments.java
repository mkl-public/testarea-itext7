package mkl.testarea.itext7.annotate;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;

/**
 * @author mkl
 */
public class ReadTextboxComments {
    /**
     * <a href="https://stackoverflow.com/questions/49880403/extract-data-from-foxit-reader-textbox-comments-using-itext">
     * Extract data from Foxit Reader textbox comments using iText
     * </a>
     * <br/>
     * <a href="http://www.mediafire.com/file/ak8iomosow8ocd4/HelloFOXIT.pdf">
     * HelloFOXIT.pdf
     * </a>
     * <p>
     * This test shows how to extract annotation contents from the OP's sample PDF.
     * </p>
     */
    @Test
    public void testReadFromHelloFOXIT() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("HelloFOXIT.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfDocument pdfDocument = new PdfDocument(pdfReader)) {
            for (int pageNr = 1; pageNr <= pdfDocument.getNumberOfPages(); pageNr++) {
                System.out.printf("\n\nPage %d\n\n", pageNr);
                PdfPage page = pdfDocument.getPage(pageNr);
                for (PdfAnnotation pdfAnnotation : page.getAnnotations()) {
                    System.out.printf("- %s\n", pdfAnnotation.getContents());
                }
            }
        }
    }
}
