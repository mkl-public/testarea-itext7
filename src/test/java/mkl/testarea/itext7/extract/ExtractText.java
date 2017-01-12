package mkl.testarea.itext7.extract;

import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;

/**
 * @author mkl
 */
public class ExtractText
{

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
    }

    /**
     * <a href="http://stackoverflow.com/questions/38119176/itextsharp-font-widths-definition-not-correctly-loaded">
     * iTextSharp font widths definition not correctly loaded
     * </a>
     * <br/>
     * <a href="https://drive.google.com/file/d/0B1RdIg0_Pbd_aTlOT2VmbnFlaTQ/view?usp=sharing">
     * itextsharp_sample_locationtext_extraction_reversing_characters.pdf
     * </a>
     * <p>
     * Indeed, iText 7 (like iText 5.5.x) does not understand <b>Encoding</b> <em>streams</em>
     * (in contrast to <em>names</em>) in composite fonts.
     * </p>
     */
    @Test
    public void testExtractItextsharpSampleLocationTextExtractionReversingCharacters() throws IOException
    {
        try (   InputStream resourceStream = getClass().getResourceAsStream("itextsharp_sample_locationtext_extraction_reversing_characters.pdf");
                PdfReader reader = new PdfReader(resourceStream);
                PdfDocument document = new PdfDocument(reader)  )
        {
            LocationTextExtractionStrategy extractionStrategy = new LocationTextExtractionStrategy();
            PdfPage page = document.getFirstPage();
            String text = PdfTextExtractor.getTextFromPage(page, extractionStrategy);
            System.out.printf("\nText from itextsharp_sample_locationtext_extraction_reversing_characters.pdf\n=====\n%s\n=====", text);
        }
    }

}
