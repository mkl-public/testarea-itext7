package mkl.testarea.itext7.content;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

/**
 * @author mkl
 */
public class HeaderFooterEvents {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/74403467/header-footer-events-not-working-as-expected">
     * Header Footer events not working as expected
     * </a>
     * <p>
     * After removing the unneeded {@link PdfDocument#addNewPage()} calls
     * from {@link HeaderFooter#generatePdf()}, something changes in the
     * header area on page 1 but is apparently covered by a filled rectangle.
     * </p>
     * <p>
     * After additionally removing the unneeded {@link PdfCanvas#rectangle(double, double, double, double)}
     * calls from {@link TextHeaderEventHandler} and {@link TextFooterEventHandler},
     * headers and footer appear as desired.
     * </p>
     */
    @Test
    public void testLikeKiranKyle() throws IOException {
        HeaderFooter main = new HeaderFooter();
        main.generatePdf();
    }

    /** @see HeaderFooterEvents#testLikeKiranKyle() */
    static class TextHeaderEventHandler implements IEventHandler {
        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfCanvas pdfCanvas = new PdfCanvas(docEvent.getPage());

            Rectangle rectangle = new Rectangle(35, 740, 520, 100);
//mkl            pdfCanvas.rectangle(rectangle);

            Canvas canvas = new Canvas(pdfCanvas, rectangle).setFontSize(7);
            // load logo image here and add
            // canvas.add(image);

            canvas.add(new Paragraph("My custom header line goes here."));

            // bottom line
            canvas.add(new Paragraph("---------------------------------------------------------------------"));
        }
    }

    /** @see HeaderFooterEvents#testLikeKiranKyle() */
    static class TextFooterEventHandler implements IEventHandler {
        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfCanvas pdfCanvas = new PdfCanvas(docEvent.getPage());

            Rectangle rectangle = new Rectangle(35, 30, 520, 50);
//mkl            pdfCanvas.rectangle(rectangle);

            Canvas canvas = new Canvas(pdfCanvas, rectangle).setFontSize(7);
            // bottom line
            canvas.add(new Paragraph("---------------------------------------------------------------------"));

            // footer text
            canvas.add(new Paragraph("My custom footer line goes here."));
        }
    }

    /**
     * https://turkogluc.com/java-creating-pdf-reports-with-itext/
     */
    /** @see HeaderFooterEvents#testLikeKiranKyle() */
    public static class HeaderFooter {
        public static void main(String[] args) throws IOException {

            HeaderFooter main = new HeaderFooter();
            main.generatePdf();
        }

        public void generatePdf() throws IOException {
            // Creating a PdfWriter
            String dest = new File(RESULT_FOLDER, "HeaderFooter.pdf").getAbsolutePath(); // "/tmp/example.pdf";
            PdfWriter writer = new PdfWriter(dest);

            // Creating a PdfDocument
            PdfDocument pdfDoc = new PdfDocument(writer);

            // Creating a Document
            Document document = new Document(pdfDoc);
            document.setFontSize(10);

//mkl            pdfDoc.addNewPage(PageSize.A4);
            document.setMargins(80, 36, 80, 36);
            pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, new TextHeaderEventHandler());
            pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new TextFooterEventHandler());

            // Adding a new page
//mkl            pdfDoc.addNewPage(PageSize.A4);
            // document.setMargins(80, 36, 60, 36);

            String content = "Lorem ipsum dolor sit amet...jjjj";
            Paragraph paragraph = new Paragraph(content);
            paragraph.setFontSize(14);
            paragraph.setTextAlignment(TextAlignment.CENTER);
            paragraph.setBorder(Border.NO_BORDER);
            paragraph.setFirstLineIndent(20);
            paragraph.setItalic();
            paragraph.setBold();
            paragraph.setBackgroundColor(new DeviceRgb(245, 245, 245));
            paragraph.setMargin(10);
            paragraph.setPaddingLeft(10);
            paragraph.setPaddingRight(10);
            paragraph.setWidth(1000);
            paragraph.setHeight(100);
            document.add(paragraph);

            int listIndex = 1;
            List list = new List();
            for (int i = 0; i < 10; i++, listIndex++) {
                list.add("Java --> " + listIndex);
                list.add("Go");
                list.add("React");
                list.add("Apache Kafka");
                list.add("Jenkins");
                list.add("Elastic Search");
            }
            document.add(list);

            // adding a table adds a rectangle into the header part
            Table table = new Table(new float[]{150F, 150F, 150F, 150F});
            table.addCell(new Cell().add(new Paragraph("Id")));
            table.addCell(new Cell().add(new Paragraph("Name")));
            table.addCell(new Cell().add(new Paragraph("Location")));
            table.addCell(new Cell().add(new Paragraph("Date")));

            table.addCell(new Cell().add(new Paragraph("1000")));
            table.addCell(new Cell().add(new Paragraph("Item-1")));
            table.addCell(new Cell().add(new Paragraph("Istanbul")));
            table.addCell(new Cell().add(new Paragraph("01/12/2020")));

            table.addCell(new Cell().add(new Paragraph("1005")));
            table.addCell(new Cell().add(new Paragraph("Item-2")));
            table.addCell(new Cell().add(new Paragraph("Warsaw")));
            table.addCell(new Cell().add(new Paragraph("05/12/2020")));
            document.add(table);

            // extra list
            list = new List();
            for (int i = 0; i < 10; i++, listIndex++) {
                list.add("Java --> " + listIndex);
                list.add("Go");
                list.add("React");
                list.add("Apache Kafka");
                list.add("Jenkins");
                list.add("Elastic Search");
            }
            document.add(list);

            // Closing the document
            document.close();
        }
    }
}
