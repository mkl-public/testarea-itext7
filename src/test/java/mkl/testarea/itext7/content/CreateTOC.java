package mkl.testarea.itext7.content;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.DottedLine;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.element.TabStop;
import com.itextpdf.layout.hyphenation.HyphenationConfig;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.TabAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.renderer.ParagraphRenderer;

/**
 * @author mkl
 */
public class CreateTOC
{
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="http://stackoverflow.com/questions/43795987/page-x-of-y-with-re-ordered-toc-x-will-start-from-1-again-after-the-toc">
     * page X of Y with re-ordered TOC: X will start from 1 again after the TOC
     * </a>
     * <p>
     * The OP's code here is fixed by adding an offset variable to the page
     * event listener and setting the offset accordingly.
     * </p>
     * <p>
     * Beware: the event timing might change or even have changed in the 7.x
     * versions; this might break the offset code. This code here has been
     * tested against the current 7.0.3-SNAPSHOT version.
     * </p>
     * @see #createOutline(PdfOutline, PdfDocument, String, String)
     * @see #createPdf(Reader, String)
     * @see PageXofY
     * @see UpdatePageRenderer
     */
    @Test
    public void testCreateTocLikeCao() throws IOException
    {
        try (   InputStream resource = getClass().getResourceAsStream("jekyll_hyde.txt");
                InputStreamReader reader = new InputStreamReader(resource)  )
        {
            File file = new File(RESULT_FOLDER, "test_toc.pdf");
            new CreateTOC().createPdf(reader, file.getAbsolutePath()); 
        }
    }

    /**
     * @see #testCreateTocLikeCao()
     */
    public void createPdf(Reader reader, String dest) throws IOException
    {
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest)); 
        pdf.getCatalog().setPageMode(PdfName.UseOutlines); 

        PageXofY event = new PageXofY(pdf);
        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, event);

        PdfFont font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN); 
        PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD); 

        Document document = new Document(pdf); 
        document.setTextAlignment(TextAlignment.JUSTIFIED) 
               .setHyphenation(new HyphenationConfig("en", "uk", 3, 3)) 
               .setFont(font) 
               .setFontSize(11); 

        event.offset = 1; // <<<

//      // add the cover
//      document.add(new Paragraph("this is the cover 1"));
//      document.add(new AreaBreak(AreaBreakType.NEXT_PAGE)); 
//      
//      
//      document.add(new Paragraph("this is the cover 2"));
//      document.add(new AreaBreak(AreaBreakType.NEXT_PAGE)); 

        // parse text to PDF 
        BufferedReader br = new BufferedReader(reader); 
        String name, line; 
        Paragraph p; 
        boolean title = true; 
        int counter = 0; 
        PdfOutline outline = null; 
        List<SimpleEntry<String,SimpleEntry<String, Integer>>> toc = new ArrayList<>(); 
        while ((line = br.readLine()) != null)
        {
            p = new Paragraph(line); 
            p.setKeepTogether(true); 
            if (title)
            {
                name = String.format("title%02d", counter++); 

                outline = createOutline(outline, pdf, line, name); 

                int pagesWithoutCover = pdf.getNumberOfPages();

                SimpleEntry<String, Integer> titlePage = new SimpleEntry(line, pagesWithoutCover); 

                p.setFont(bold).setFontSize(12) 
                          .setKeepWithNext(true) 
                          .setDestination(name)
                          .setNextRenderer(new UpdatePageRenderer(p, titlePage)); 
                title = false; 

                document.add(p); 
                toc.add(new SimpleEntry(name, titlePage)); 
            } 
            else
            {
                p.setFirstLineIndent(18); 
                if (line.isEmpty())
                { 
                    p.setMarginBottom(12); 
                    title = true; 
                } 
                else
                {
                    p.setMarginBottom(0); 
                } 
                document.add(p); 
            } 
        } 

        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE)); 

        event.offset = 0; // <<<

        // create table of contents 
        int startToc = pdf.getNumberOfPages();        
        p = new Paragraph().setFont(bold).add("Table of Contents").setDestination("toc");
        document.add(p); 
        toc.remove(0); 
        List<TabStop> tabstops = new ArrayList(); 
        tabstops.add(new TabStop(580, TabAlignment.RIGHT, new DottedLine())); 
        for (SimpleEntry<String, SimpleEntry<String, Integer>> entry : toc)
        { 
            SimpleEntry<String, Integer> text = entry.getValue(); 
            p = new Paragraph() 
                 .addTabStops(tabstops) 
                 .add(text.getKey())
//               .setFixedLeading(150)
                 .add(new Tab()) 
                 .add(String.valueOf(text.getValue())) 
                 .setAction(PdfAction.createGoTo(entry.getKey())); 

            document.add(p); 
        } 
        int tocPages = pdf.getNumberOfPages() - startToc; 

        // reorder pages 
        PdfPage page; 
        for (int i = 0; i <= tocPages; i++)
        { 
            page = pdf.getPage(startToc + i);
            pdf.removePage(startToc + i); 
            pdf.addPage(i + 1, page); 
        } 

        event.writeTotal(pdf);
        document.close(); 
    } 

    /**
     * @see #testCreateTocLikeCao()
     */
    protected class UpdatePageRenderer extends ParagraphRenderer
    { 
        protected SimpleEntry<String, Integer> entry; 

        public UpdatePageRenderer(Paragraph modelElement, SimpleEntry<String, Integer> entry)
        { 
            super(modelElement); 

            this.entry = entry; 
        } 

        @Override 
        public LayoutResult layout(LayoutContext layoutContext)
        { 
            LayoutResult result = super.layout(layoutContext); 
            entry.setValue(layoutContext.getArea().getPageNumber()); 
            return result; 
        } 
    } 

    /**
     * @see #testCreateTocLikeCao()
     */
    public PdfOutline createOutline(PdfOutline outline, PdfDocument pdf, String title, String name)
    { 
        if (outline ==  null)
        { 
            outline = pdf.getOutlines(false); 
            outline = outline.addOutline(title); 
            outline.addDestination(PdfDestination.makeDestination(new PdfString(name))); 
            return outline; 
        } 

        PdfOutline kid = outline.addOutline(title); 
        kid.addDestination(PdfDestination.makeDestination(new PdfString(name))); 
        return outline; 
    } 

    /**
     * @see #testCreateTocLikeCao()
     */
    protected class PageXofY implements IEventHandler
    {
        int offset = 0; // <<<

        protected PdfFormXObject placeholder;
        protected float side = 20;
        protected float x = 300;
        protected float y = 25;
        protected float space = 4.5f;
        protected float descent = 3;

        public PageXofY(PdfDocument pdf)
        {
            placeholder = new PdfFormXObject(new Rectangle(0, 0, side, side));
        }

        @Override
        public void handleEvent(Event event)
        {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdf = docEvent.getDocument();
            PdfPage page = docEvent.getPage();
            int pageNumber = pdf.getPageNumber(page); 

            Rectangle pageSize = page.getPageSize();
            PdfCanvas pdfCanvas = new PdfCanvas(
                page.newContentStreamBefore(), page.getResources(), pdf);
            Canvas canvas = new Canvas(pdfCanvas, pageSize);
            Paragraph p = new Paragraph().add("Page ").add(String.valueOf(pageNumber + offset)).add(" of"); // <<<

            canvas.showTextAligned(p, x, y, TextAlignment.RIGHT);
            pdfCanvas.addXObject(placeholder, x + space, y - descent);
            pdfCanvas.release();
        }

        public void writeTotal(PdfDocument pdf)
        {
            Canvas canvas = new Canvas(placeholder, pdf);
            canvas.showTextAligned(String.valueOf(pdf.getNumberOfPages()),
                0, descent, TextAlignment.LEFT);
        }
    }
}
