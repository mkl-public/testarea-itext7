package mkl.testarea.itext7.extract;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IPdfTextLocation;
import com.itextpdf.kernel.pdf.canvas.parser.listener.RegexBasedLocationExtractionStrategy;

/**
 * @author mkl
 */
public class CheckExtractedPosition {
    /**
     * <a href="https://stackoverflow.com/questions/72148786/why-x-y-coordinates-returned-by-itext-differ-for-the-same-text-sitting-in-the">
     * Why X,Y-coordinates (returned by iText) differ for the same text sitting in the same spot on different pdf pages?
     * </a>
     * <br/>
     * <a href="https://www.sars.gov.za/wp-content/uploads/Legal/SCEA1964/LAPD-LPrim-Tariff-2012-04-Schedule-No-1-Part-1-Chapters-1-to-99.pdf">
     * LAPD-LPrim-Tariff-2012-04-Schedule-No-1-Part-1-Chapters-1-to-99.pdf
     * </a>
     * <p>
     * Cannot reproduce the issue. As it turned out, though, the OP actually had re-used the
     * {@link PdfCanvasProcessor} for multiple pages without resetting it. This caused the
     * issue at hand.
     * </p>
     */
    @Test
    public void testSitAnkosFile() throws IOException {
        try (
            InputStream resource = getClass().getResourceAsStream("LAPD-LPrim-Tariff-2012-04-Schedule-No-1-Part-1-Chapters-1-to-99.pdf");
            PdfReader pdfReader = new PdfReader(resource);
            PdfDocument pdfDocument = new PdfDocument(pdfReader)
        ) {
            for (int page = 1; page <= pdfDocument.getNumberOfPages(); page++) {
                RegexBasedLocationExtractionStrategy extractionStrategy = new RegexBasedLocationExtractionStrategy("Date: \\d{4}-\\d{2}-\\d{2}");
                new PdfCanvasProcessor(extractionStrategy).processPageContent(pdfDocument.getPage(page));
                List<IPdfTextLocation> locations = new ArrayList<>(extractionStrategy.getResultantLocations());
                System.out.printf("Page %d: \n", page);
                for (IPdfTextLocation location : locations) {
                    Rectangle rectangle = location.getRectangle();
                    System.out.printf("   %f, %f %f x %f: %s\n", rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight(), location.getText());
                }
            }
        }
    }
}
