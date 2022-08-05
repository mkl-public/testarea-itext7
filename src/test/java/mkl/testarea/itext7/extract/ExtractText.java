package mkl.testarea.itext7.extract;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.filter.TextRegionEventFilter;
import com.itextpdf.kernel.pdf.canvas.parser.listener.FilteredTextEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.GlyphTextEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;

/**
 * @author mkl
 */
public class ExtractText
{
    final static File RESULT_FOLDER = new File("target/test-outputs", "extract");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
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

    /**
     * <a href="https://stackoverflow.com/questions/49781516/itext-coordinate-outside-allowed-range-exception-using-locationtextlocationstr">
     * iText “Coordinate outside allowed range” exception using LocationTextLocationStrategy
     * </a>
     * <br/>
     * <a href="https://drive.google.com/file/d/1m4X8HTrR1Ssh7XtiTbARnI_-3s5FalrX/view?usp=sharing">
     * 1.pdf
     * </a> as GustavoPiucco-1.pdf
     * <p>
     * Indeed, the exception can be reproduced here but not for the other file,
     * cf. {@link #testExtractGustavoPiucco2()}.
     * </p>
     * <p>
     * The cause are clip path rectangles 79026 default user units in height.
     * </p>
     */
    @Test
    public void testExtractGustavoPiucco1() throws IOException
    {
        try (   InputStream resourceStream = getClass().getResourceAsStream("GustavoPiucco-1.pdf");
                PdfReader reader = new PdfReader(resourceStream);
                PdfDocument document = new PdfDocument(reader)  )
        {
            StringBuilder sb = new StringBuilder();
            for (int pageNum = 1; pageNum <= document.getNumberOfPages(); pageNum++)
            {
                PdfPage page = document.getPage(pageNum);
                sb.append(PdfTextExtractor.getTextFromPage(page, new LocationTextExtractionStrategy()));
            }

            System.out.printf("\nText from GustavoPiucco-1.pdf\n=====\n%s\n=====", sb);
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/49781516/itext-coordinate-outside-allowed-range-exception-using-locationtextlocationstr">
     * iText “Coordinate outside allowed range” exception using LocationTextLocationStrategy
     * </a>
     * <br/>
     * <a href="https://drive.google.com/file/d/1S1KFaEijsSLQe7RyZDfq_fegfUtploST/view?usp=sharing">
     * 2.pdf
     * </a> as GustavoPiucco-2.pdf
     * <p>
     * Indeed, the exception cannot be reproduced here but it can for the other file,
     * cf. {@link #testExtractGustavoPiucco1()}.
     * </p>
     * <p>
     * Here the corresponding clip path rectangles are "only" 33628 default user units in height.
     * </p>
     */
    @Test
    public void testExtractGustavoPiucco2() throws IOException
    {
        try (   InputStream resourceStream = getClass().getResourceAsStream("GustavoPiucco-2.pdf");
                PdfReader reader = new PdfReader(resourceStream);
                PdfDocument document = new PdfDocument(reader)  )
        {
            StringBuilder sb = new StringBuilder();
            for (int pageNum = 1; pageNum <= document.getNumberOfPages(); pageNum++)
            {
                PdfPage page = document.getPage(pageNum);
                sb.append(PdfTextExtractor.getTextFromPage(page, new LocationTextExtractionStrategy()));
            }

            System.out.printf("\nText from GustavoPiucco-2.pdf\n=====\n%s\n=====", sb);
        }
    }

    /**
     * <a href="https://issues.apache.org/jira/browse/PDFBOX-5023">
     * OpenType Layout tables used in font ArabicTransparent-ARABIC are not implemented in PDFBox and will be ignored
     * </a>
     * <br/>
     * <a href="https://issues.apache.org/jira/secure/attachment/13015751/pdfsample.pdf">
     * pdfsample.pdf
     * </a>
     * <p>
     * iText text extraction throws an exception here, it is caused by the
     * ROS Adobe-Arabic1-0 which iText does not support. Apparently that ROS
     * also is not known to the "CMap resources for Adobe’s public character
     * collections" github project...
     * </p>
     */
    @Test
    public void testExtractPdfSample() throws IOException
    {
        try (   InputStream resourceStream = getClass().getResourceAsStream("pdfsample.pdf");
                PdfReader reader = new PdfReader(resourceStream);
                PdfDocument document = new PdfDocument(reader)  )
        {
            StringBuilder sb = new StringBuilder();
            for (int pageNum = 1; pageNum <= document.getNumberOfPages(); pageNum++)
            {
                PdfPage page = document.getPage(pageNum);
                sb.append(PdfTextExtractor.getTextFromPage(page, new LocationTextExtractionStrategy()));
            }

            System.out.printf("\nText from pdfsample.pdf\n=====\n%s\n=====", sb);
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/73247134/extract-text-from-pdf-by-a-specific-area">
     * Extract text from pdf by a specific area
     * </a>
     * <br/>
     * <a href="https://www.docdroid.net/YsBTKuc/exe-pdf">
     * exe.pdf
     * </a>
     * <p>
     * iText text extraction filtering works for the full drawn string of text showing
     * instructions, i.e. if some part of the drawn string is in the area of the region
     * filter, the whole string is accepted. If one wants glyph-wise filtering instead,
     * one should wrap the filtered extraction strategy in a {@link GlyphTextEventListener}.
     * </p>
     */
    @Test
    public void testExtractLikeAnassLaraki() throws IOException {
        try (
            InputStream resourceStream = getClass().getResourceAsStream("exe.pdf");
            PdfReader pdfRead = new PdfReader(resourceStream);
            PdfWriter pdfWriter = new PdfWriter(new File(RESULT_FOLDER, "exe-withRect.pdf"));
            PdfDocument pdfdoc = new PdfDocument(pdfRead, pdfWriter)
        ) {
            Rectangle rect = new Rectangle((float)4.090625, (float)58.384375, (float)76.60625, (float)40.1625);
            TextRegionEventFilter regionFilter = new TextRegionEventFilter(rect);

            for (int page = 1; page <= pdfdoc.getNumberOfPages(); page++)
            {
                ITextExtractionStrategy strat = new FilteredTextEventListener(new LocationTextExtractionStrategy(), regionFilter);
                String str = PdfTextExtractor.getTextFromPage(pdfdoc.getPage(page), strat);
                System.out.printf("\nPage %d:\n----\n%s\n----\n", page, str);

                strat = new GlyphTextEventListener(new FilteredTextEventListener(new LocationTextExtractionStrategy(), regionFilter));
                str = PdfTextExtractor.getTextFromPage(pdfdoc.getPage(page), strat);
                System.out.printf("\nglyphwise extraction:\n----\n%s\n----\n", str);

                PdfCanvas pdfCanvas = new PdfCanvas(pdfdoc.getPage(page), true);
                pdfCanvas.setStrokeColorRgb(1, 0, 0);
                pdfCanvas.rectangle(rect);
                pdfCanvas.stroke();
            }
        }
    }
}
