package mkl.testarea.itext7.meta;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;

/**
 * @author mkl
 */
public class OpenFile {
    /**
     * <a href="https://stackoverflow.com/questions/65581194/tag-structure-initialization-failed-tag-structure-is-ignored-it-might-be-corru">
     * Tag structure initialization failed, tag structure is ignored, it might be corrupted
     * </a>
     * <br/>
     * <a href="https://drive.google.com/file/d/1KO772GQbolJ0oNMAzsdGPrLqfUtRcKbs/view?usp=sharing">
     * omb_0970_0222_a_wip.pdf
     * </a>
     * <p>
     * Cannot reproduce the issue.
     * </p>
     */
    @Test
    public void testOpenOmb_0970_0222_a_wip() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("omb_0970_0222_a_wip.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfDocument pdfDocument = new PdfDocument(pdfReader)) {
            System.out.println(pdfDocument.getDocumentInfo().getAuthor());
        }
    }
}
