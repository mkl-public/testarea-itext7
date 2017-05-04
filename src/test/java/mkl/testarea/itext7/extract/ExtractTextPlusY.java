package mkl.testarea.itext7.extract;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;

/**
 * @author mkl
 */
public class ExtractTextPlusY
{
    /**
     * <a href="http://stackoverflow.com/questions/43746884/how-to-get-the-text-position-from-the-pdf-page-in-itext-7">
     * How to get the text position from the pdf page in iText 7
     * </a>
     * <p>
     * This method shows how to extract text with its characters' respective y coordinates
     * from a document. It makes use of {@link TextPlusYExtractionStrategy}, a strategy
     * derived from the {@link LocationTextExtractionStrategy} tweaked to also return
     * a {@link TextPlusY} instance which contains text plus y coordinates. 
     * </p>
     * <p>
     * Beware, this is but a proof-of-concept which in particular assumes text to be written
     * horizontally, i.e. using an effective transformation matrix with b and c equal to 0.
     * Furthermore the character and coordinate retrieval methods of {@link TextPlusY} are
     * not at all optimized and might take long to execute.
     * </p>
     */
    @Test
    public void testExtractTextPlusYFromTest() throws IOException
    {
        try (   InputStream resourceStream = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf");
                PdfReader reader = new PdfReader(resourceStream);
                PdfDocument document = new PdfDocument(reader)  )
        {
            TextPlusYExtractionStrategy extractionStrategy = new TextPlusYExtractionStrategy();
            PdfPage page = document.getFirstPage();

            PdfCanvasProcessor parser = new PdfCanvasProcessor(extractionStrategy);
            parser.processPageContent(page);
            TextPlusY textPlusY = extractionStrategy.getResultantTextPlusY();

            System.out.printf("\nText from test.pdf\n=====\n%s\n=====\n", textPlusY);

            System.out.print("\nText with y from test.pdf\n=====\n");
            
            int length = textPlusY.length();
            float lastY = Float.MIN_NORMAL;
            for (int i = 0; i < length; i++)
            {
                float y = textPlusY.yCoordAt(i);
                if (y != lastY)
                {
                    System.out.printf("\n(%4.1f) ", y);
                    lastY = y;
                }
                System.out.print(textPlusY.charAt(i));
            }
            System.out.print("\n=====\n");
        }
    }
}
