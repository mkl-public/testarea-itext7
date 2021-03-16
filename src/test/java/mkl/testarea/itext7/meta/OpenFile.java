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

    /**
     * <a href="https://stackoverflow.com/questions/66583475/pdf-becoming-corrupted-after-signing">
     * PDF becoming corrupted after signing
     * </a>
     * <br/>
     * <a href="https://ufile.io/uxq3gr6o">
     * xrefstreamdoc (17).pdf
     * </a>
     * <p>
     * There are multiple issues in the cross reference stream of this file.
     * In particular there are less entries (22) than claimed (23), there is
     * a shift (entry for object 1 points to object 2 etc), and there are even
     * more objects in the file (at least an object 24).
     * </p>
     */
    @Test
    public void testOpenXrefstreamdoc17() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("xrefstreamdoc (17).pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfDocument pdfDocument = new PdfDocument(pdfReader)) {
            System.out.println(pdfDocument.getDocumentInfo().getAuthor());
        }
    }
}
