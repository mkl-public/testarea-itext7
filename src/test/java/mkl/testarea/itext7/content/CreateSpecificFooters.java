package mkl.testarea.itext7.content;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

/**
 * @author mkl
 */
public class CreateSpecificFooters {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/50236085/itext-7-different-footer-on-last-page-null-pointer-in-pdfdictionary">
     * iText 7 - Different footer on last page - Null pointer in PdfDictionary
     * </a>
     * <p>
     * This test shows how to use different footers for special pages (e.g.
     * the last page) even without setting immediateFlush to false and looping
     * over the pages in the end.
     * </p>
     */
    @Test
    public void testDifferentLastPageFooter() throws FileNotFoundException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new File(RESULT_FOLDER, "differentLastPageFooter.pdf")));
        Document doc = new Document(pdfDoc);
        TextFooterEventHandler eventHandler = new TextFooterEventHandler(doc);
        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, eventHandler);
        for (int i = 0; i < 12; i++) {
            doc.add(new Paragraph("Test " + i + " Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet."));
        }

        // the currently last page is also the last page of the document, so inform the event listener 
        eventHandler.lastPage = pdfDoc.getLastPage();
        doc.close();
    }

    /**
     * A footer generating event handler which creates a different footer on the
     * last page. It requires the page object to be explicitly set in its lastPage
     * member variable. It does not suffice to simply inform the listener that all
     * content has been added to the document as the page events may be triggered
     * with some delay.
     */
    static class TextFooterEventHandler implements IEventHandler {
        Document doc;
        PdfPage lastPage = null;

        public TextFooterEventHandler(Document doc) {
            this.doc = doc;
        }

        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfCanvas canvas = new PdfCanvas(docEvent.getPage());
            Rectangle pageSize = docEvent.getPage().getPageSize();
            canvas.beginText();
            try {
                canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE), 5);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (lastPage == docEvent.getPage()) {
                canvas.moveText((pageSize.getRight() - doc.getRightMargin() - (pageSize.getLeft() + doc.getLeftMargin())) / 2 + doc.getLeftMargin(), pageSize.getTop() - doc.getTopMargin() + 10)
                    .showText("VERY SPAECIAL LAST PAGE HEADER")
                    .moveText(0, (pageSize.getBottom() + doc.getBottomMargin()) - (pageSize.getTop() - doc.getTopMargin()) - 20)
                    .showText("VERY SPAECIAL LAST PAGE FOOTER")
                    .endText()
                    .release();
            } else {
                canvas.moveText((pageSize.getRight() - doc.getRightMargin() - (pageSize.getLeft() + doc.getLeftMargin())) / 2 + doc.getLeftMargin(), pageSize.getTop() - doc.getTopMargin() + 10)
                    .showText("this is a header")
                    .moveText(0, (pageSize.getBottom() + doc.getBottomMargin()) - (pageSize.getTop() - doc.getTopMargin()) - 20)
                    .showText("this is a footer")
                    .endText()
                    .release();
            }
        }
    }    
}
