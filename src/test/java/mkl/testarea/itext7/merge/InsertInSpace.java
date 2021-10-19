package mkl.testarea.itext7.merge;

import static com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.PdfDocumentContentParser;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

/**
 * @author mkl
 */
public class InsertInSpace {
    final static File RESULT_FOLDER = new File("target/test-outputs", "merge");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/56522616/itext-7-add-source-pdf-content-to-destination-pdf">
     * IText 7 + Add source PDF content to destination PDF
     * </a>
     * <p>
     * This test reduces the page size of the source document
     * page to its bounding box which is determined using the
     * {@link MarginFinder} content parsing event listener.
     * Then it forwards that page to insertion into the OP's
     * document with header and footer generation.
     * </p>
     * @see #insertIntoNithinTestFile(PdfPage, String)
     * @see MarginFinder
     */
    @Test
    public void testInsertSimpleTestPdf() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfDocument pdfDocument = new PdfDocument(pdfReader)    ) {
            PdfDocumentContentParser contentParser = new PdfDocumentContentParser(pdfDocument);
            MarginFinder strategy = contentParser.processContent(1, new MarginFinder());

            PdfPage page = pdfDocument.getPage(1);
            page.setCropBox(strategy.getBoundingBox());
            page.setMediaBox(strategy.getBoundingBox());
            insertIntoNithinTestFile(page, "test-InsertIntoNithinTestFile.pdf");
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/56522616/itext-7-add-source-pdf-content-to-destination-pdf">
     * IText 7 + Add source PDF content to destination PDF
     * </a>
     * <p>
     * This essentially is the OP's code without the Paths magic
     * and without test content but with an appropriate top margin
     * value and an insertion of the given page.
     * </p>
     * @see #testInsertSimpleTestPdf()
     */
    void insertIntoNithinTestFile(PdfPage sourcePage, String resultName) {
        String uuid = UUID.randomUUID().toString();
        try {
            @SuppressWarnings("resource")
            PdfWriter writer = new PdfWriter(new FileOutputStream(new File(RESULT_FOLDER, resultName))).setSmartMode(true);
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.setDefaultPageSize(PageSize.A4.rotate());
            String fonts[] = { "c:\\Windows\\Fonts/TREBUC.TTF", "c:\\Windows\\Fonts/TREBUCBD.TTF", "c:\\Windows\\Fonts/TREBUCBI.TTF", "c:\\Windows\\Fonts/TREBUCIT.TTF" };
            FontProvider fontProvider = new FontProvider();
            Map<String, PdfFont> pdfFontMap = new HashMap();
            for (String font : fonts) {
                FontProgram fontProgram = FontProgramFactory.createFont(font);
                if (font.endsWith("TREBUC.TTF")) {
                    pdfFontMap.put("NORMAL", PdfFontFactory.createFont(fontProgram, PdfEncodings.WINANSI, PREFER_EMBEDDED));
                } else if (font.endsWith("TREBUCBD.TTF")) {
                    pdfFontMap.put("BOLD", PdfFontFactory.createFont(fontProgram, PdfEncodings.WINANSI, PREFER_EMBEDDED));
                } else if (font.endsWith("TREBUCBI.TTF")) {
                    pdfFontMap.put("BOLD_ITALIC", PdfFontFactory.createFont(fontProgram, PdfEncodings.WINANSI, PREFER_EMBEDDED));
                } else if (font.endsWith("TREBUCIT.TTF")) {
                    pdfFontMap.put("ITALIC", PdfFontFactory.createFont(fontProgram, PdfEncodings.WINANSI, PREFER_EMBEDDED));
                }

                fontProvider.addFont(fontProgram);
            }

            NormalPageHeader headerHandler = new NormalPageHeader( "src\\test\\resources\\mkl\\testarea\\itext7\\form\\2x2colored.png", pdfFontMap);
            pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, headerHandler);
            PageEndEvent pageEndEvent = new PageEndEvent( "src\\test\\resources\\mkl\\testarea\\itext7\\annotate\\Willi-1.jpg", pdfFontMap);
            pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, pageEndEvent);

            Document doc = new Document(pdfDoc);
            doc.setTopMargin(55);
            PdfFormXObject xobject = sourcePage.copyAsFormXObject(pdfDoc);
            Rectangle xobjectBoundaryBox = xobject.getBBox().toRectangle();
            xobject.getPdfObject().put(PdfName.Matrix, new PdfArray(new float[] {1, 0, 0, 1, -xobjectBoundaryBox.getLeft(), -xobjectBoundaryBox.getBottom()}));
            Image image = new Image(xobject);
            image.setAutoScale(true);
            doc.add(image);

            doc.close();
            System.out.println("Converted to PDF Succesfully >>> convertedSvg_" + uuid + ".pdf");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error Occured while converting to PDF = " + e.getMessage());
        }
    }

    /**
     * @see InsertInSpace#insertIntoNithinTestFile(PdfPage, String)
     */
    class NormalPageHeader implements IEventHandler {
        String header;
        Map<String, PdfFont> font;

        public NormalPageHeader(String header, Map<String, PdfFont> font) {
            this.header = header;
            this.font = font;
        }

        @Override
        public void handleEvent(Event event) {
            // Retrieve document and
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdf = docEvent.getDocument();
            PdfPage page = docEvent.getPage();
            Rectangle pageSize = page.getPageSize();
            PdfCanvas pdfCanvas = new PdfCanvas(page.getLastContentStream(), page.getResources(), pdf);
            Canvas canvas = new Canvas(pdfCanvas, pageSize);
            canvas.setFontSize(10f);
            Table table = new Table(3);
            table.setBorder(Border.NO_BORDER);
            table.setWidth(UnitValue.createPercentValue(100));
            Cell leftCell = new Cell();
            leftCell.setFont(font.get("NORMAL"));
            leftCell.setPaddingTop(15);
            leftCell.setPaddingLeft(20);
            leftCell.setBorder(Border.NO_BORDER);
            leftCell.setBorderBottom(new SolidBorder(0.5f));
            leftCell.setWidth(UnitValue.createPercentValue(33.3f));
            Text userLabel = new Text("Username: ");
            userLabel.setBold();
            Paragraph paragraph = new Paragraph(userLabel);
            Cell middleCell = new Cell();
            middleCell.setFont(font.get("NORMAL"));
            middleCell.setPaddingTop(15);
            middleCell.setBorder(Border.NO_BORDER);
            middleCell.setBorderBottom(new SolidBorder(0.5f));
            middleCell.setWidth(UnitValue.createPercentValue(33.3f));
            paragraph = new Paragraph("Main Header");
            paragraph.setTextAlignment(TextAlignment.CENTER);
            paragraph.setBold();
            paragraph.setFontSize(12);
            middleCell.add(paragraph);
            String programString = "Sample header";
            paragraph = new Paragraph(programString);
            paragraph.setTextAlignment(TextAlignment.CENTER);
            paragraph.setBold();
            paragraph.setFontSize(10);
            middleCell.add(paragraph);

            table.addCell(middleCell);
            Cell rightCell = new Cell();
            rightCell.setFont(font.get("NORMAL"));
            rightCell.setPaddingTop(20);
            rightCell.setWidth(UnitValue.createPercentValue(33.3f));
            rightCell.setHorizontalAlignment(HorizontalAlignment.RIGHT);
            rightCell.setBorder(Border.NO_BORDER);
            rightCell.setBorderBottom(new SolidBorder(0.5f));
            rightCell.setPaddingRight(20);
            // Write text at position
            Image img;
            try {
                img = new Image(ImageDataFactory.create(header));
                img.setHorizontalAlignment(HorizontalAlignment.RIGHT);
                Style style = new Style();
                style.setWidth(91);
                style.setHeight(25);

                img.addStyle(style);
                rightCell.add(img);
                table.addCell(rightCell);
                table.setMarginLeft(15);
                table.setMarginRight(15);
                canvas.add(table);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @see InsertInSpace#insertIntoNithinTestFile(PdfPage, String)
     */
    class PageEndEvent implements IEventHandler {
        protected PdfFormXObject placeholder;
        protected float side = 20;
        protected float x = 300;
        protected float y = 10;
        protected float space = 4.5f;
        private String bar;
        protected float descent = 3;
        Map<String, PdfFont> font;

        public PageEndEvent(String bar, Map<String, PdfFont> font) {
            this.bar = bar;
            this.font = font;
            placeholder = new PdfFormXObject(new Rectangle(0, 0, side, side));
        }

        @Override
        public void handleEvent(Event event) {
            Table table = new Table(3);
            table.setBorder(Border.NO_BORDER);
            table.setWidth(UnitValue.createPercentValue(100));
            Cell confCell = new Cell();
            confCell.setFont(font.get("NORMAL"));
            confCell.setPaddingTop(15);
            confCell.setPaddingLeft(20);
            confCell.setBorder(Border.NO_BORDER);
            confCell.setBorderBottom(new SolidBorder(0.5f));
            confCell.setWidth(UnitValue.createPercentValue(100));
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdf = docEvent.getDocument();
            PdfPage page = docEvent.getPage();
            Rectangle pageSize = page.getPageSize();
            PdfCanvas pdfCanvas = new PdfCanvas(page.getLastContentStream(), page.getResources(), pdf);
            Canvas canvas = new Canvas(pdfCanvas, pageSize);
            Image img;
            try {
                img = new Image(ImageDataFactory.create(bar));
                img.setHorizontalAlignment(HorizontalAlignment.LEFT);
                Style style = new Style();
                style.setWidth(UnitValue.createPercentValue(100));
                style.setHeight(50);
                img.addStyle(style);
                Paragraph p = new Paragraph().add("Test: Confidential");
                p.setFont(font.get("NORMAL"));
                p.setFontSize(8);
                p.setFontColor(com.itextpdf.kernel.colors.ColorConstants.GRAY);
                canvas.showTextAligned(p, x, y, TextAlignment.CENTER);
                pdfCanvas.addXObject(placeholder, x + space, y - descent);
                pdfCanvas.release();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        public void writeTotal(PdfDocument pdf) {
            Canvas canvas = new Canvas(placeholder, pdf);
            canvas.showTextAligned(String.valueOf(pdf.getNumberOfPages()), 0, descent, TextAlignment.LEFT);
        }
    }
}
