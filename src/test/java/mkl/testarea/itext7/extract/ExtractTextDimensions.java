/**
 * 
 */
package mkl.testarea.itext7.extract;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.CharacterRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.FilteredEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IPdfTextLocation;
import com.itextpdf.kernel.pdf.canvas.parser.listener.RegexBasedLocationExtractionStrategy;

/**
 * @author mklink
 *
 */
public class ExtractTextDimensions {
    /**
     * <a href="https://stackoverflow.com/questions/70789651/how-to-find-the-height-and-width-of-a-text-in-a-rotated-pdf-where-the-text-is-al">
     * How to find the height and width of a text in a rotated pdf where the text is also rotated by same angle?
     * </a>
     * <br/>
     * <a href="https://drive.google.com/file/d/1AUhsaWz3qwj5zbOgQ9r3cOAXDsHSLB1s/view?usp=sharing">
     * Before.pdf
     * </a> as "BeforeAkhilNagaSai.pdf"
     * <p>
     * Cannot reproduce the OP's issue. The width and height outputs are clearly not zero.
     * Eventually it turned out that the problem exists only for older iText versions (7.1.14'ish).
     * </p>
     */
    @Test
    public void testBeforeAkhilNagaSai() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/content/BeforeAkhilNagaSai.pdf")) {
            PdfDocument pdfDoc = new PdfDocument(new PdfReader(resource));
            int pages = pdfDoc.getNumberOfPages();
            FilteredEventListener listener = new FilteredEventListener();
            String regex = "The text to be rotated";
            for (int x = 1; x <= pages; x++)
            {
                RegexBasedLocationExtractionStrategy extractionStrategy = listener.attachEventListener(new RegexBasedLocationExtractionStrategy(Pattern.compile(regex, Pattern.CASE_INSENSITIVE))
                {
                    protected List<Rectangle> toRectangles(List<CharacterRenderInfo> cris)
                    {
                        List<Rectangle> rectangles = super.toRectangles(cris);
                        if (rectangles.size() == 1)
                        {
                            return rectangles;
                        }
                        else
                        {
                            return new ArrayList<>();
                        }
                    }
                });
                PdfPage currentPage = pdfDoc.getPage(x);
                    new PdfCanvasProcessor(listener).processPageContent(currentPage);

                Collection<IPdfTextLocation> eL = extractionStrategy.getResultantLocations();
                Iterator<IPdfTextLocation> eLItr = eL.iterator();
                while(eLItr.hasNext())
                {
                    IPdfTextLocation location = eLItr.next();
                    int rotation = currentPage.getRotation();
                    
                    System.out.println("The rotation is:"+rotation);
                    
                    double x_value = location.getRectangle().getX();
                    double y_value = location.getRectangle().getY();
                    double width = location.getRectangle().getWidth();
                    double height = location.getRectangle().getHeight();

                    System.out.println("X :"+x_value);
                    System.out.println("Y :"+y_value);
                    System.out.println("Height :"+width);
                    System.out.println("Width :"+height);
                }
            }
        }
    }

}
