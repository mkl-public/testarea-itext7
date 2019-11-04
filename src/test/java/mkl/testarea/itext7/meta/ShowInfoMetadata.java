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

    /**
     * <a href="https://stackoverflow.com/questions/58659870/itext-7-1-8-cant-read-some-pdf-file">
     * iText 7.1.8 can't read some pdf file
     * </a>
     * <br/>
     * <a href="https://drive.google.com/open?id=1AVda9qst-DiVbz4QFQh8iIv2aLn-uXRR">
     * x001.pdf
     * </a>
     * <p>
     * The PDF here is processed in 81 seconds. In particular it's
     * not stuck, and its also not a 12 minutes wait here in Java.
     * </p>
     */
    @Test
    public void testX001() throws IOException {
        System.out.printf("\n%s\n", "x001.pdf");
        long start = System.currentTimeMillis();
        try (   InputStream resource = getClass().getResourceAsStream("x001.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfDocument pdfDocument = new PdfDocument(pdfReader)) {
            showMetadata(pdfDocument);
        }
        long end = System.currentTimeMillis();
        System.out.printf("- test took %s seconds\n", (end-start)/1000f);
    }
}
