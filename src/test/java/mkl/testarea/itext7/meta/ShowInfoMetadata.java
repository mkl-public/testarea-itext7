package mkl.testarea.itext7.meta;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;

/**
 * @author mkl
 */
public class ShowInfoMetadata {
    /**
     * <a href="https://stackoverflow.com/questions/49529301/get-all-metadata-from-an-existing-pdf-using-itext7">
     * Get all metadata from an existing PDF using iText7
     * </a>
     * <p>
     * This test shows how to extract all Info dictionary data.
     * </p>
     */
    @Test
    public void testAppearance_TestingTest854729() throws IOException {
        System.out.printf("\n%s\n", "Appearance_Testing Test854729.pdf");
        try (   InputStream resource = getClass().getResourceAsStream("Appearance_Testing Test854729.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfDocument pdfDocument = new PdfDocument(pdfReader)) {
            showMetadata(pdfDocument);
        }
    }

    public void showMetadata(PdfDocument pdfDocument) {
        PdfDictionary infoDictionary = pdfDocument.getTrailer().getAsDictionary(PdfName.Info);
        for (PdfName key : infoDictionary.keySet())
            System.out.printf("* %s -> %s\n", key, infoDictionary.getAsString(key));
    }
}
