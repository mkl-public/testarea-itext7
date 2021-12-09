package mkl.testarea.itext7.encryption;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;

/**
 * @author mklink
 */
public class DecryptFile {
    /**
     * <a href="https://stackoverflow.com/questions/70265614/issue-merging-pdfs-on-itext7">
     * Issue merging PDFs on iText7
     * </a>
     * <br/>
     * <a href="https://www.dropbox.com/s/w7rti30jvm4pjzc/RN%202104812.pdf?dl=0">
     * RN 2104812.pdf
     * </a>
     * <p>
     * Indeed, here iText incorrectly requires a Length value in the encryption
     * dictionary even though it is optional and only specified for V values 2 and
     * 3 while here V is 1.
     * </p>
     */
    @Test
    public void testRn2104812() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("RN 2104812.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfDocument pdfDocument = new PdfDocument(pdfReader)) {
            System.out.println(pdfDocument.getDocumentInfo().getAuthor());
        }
    }

}
