package mkl.testarea.itext7.content;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;

/**
 * @author mkl
 */
public class ShowTextAtPosition {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/57602156/how-to-add-paragraph-and-border-using-openpdf">
     * How to add paragraph and border using OpenPDF
     * </a>
     * <p>
     * This test shows how to create a vertically and horizontally
     * centered paragraph with a dark blue border.
     * </p>
     */
    @Test
    public void testShowCenteredBorderedParagraph() throws IOException {
        String firstName = "Mister";
        String lastName = "Nine";
        try (   PdfWriter pdfWriter = new PdfWriter(new File(RESULT_FOLDER, "CenterParagraph.pdf"));
                PdfDocument pdfDocument = new PdfDocument(pdfWriter);
                Document document = new Document(pdfDocument)   ) {
            Paragraph paragraph = new Paragraph("Hello! This PDF is created for "+firstName+" "+lastName);
            paragraph.setWidth(100).setBorder(new SolidBorder(new DeviceRgb(0f, 0f, 0.6f), 3));
            PageSize box = pdfDocument.getDefaultPageSize();
            document.showTextAligned(paragraph, (box.getLeft() + box.getRight()) / 2, (box.getTop() + box.getBottom()) / 2,
                    TextAlignment.CENTER, VerticalAlignment.MIDDLE);
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/57602156/how-to-add-paragraph-and-border-using-openpdf">
     * How to add paragraph and border using OpenPDF
     * </a>
     * <p>
     * This test shows how to create a vertically and horizontally
     * centered paragraph, decorated with header and footer images
     * and surrounded by a dark blue border.
     * </p>
     */
    @Test
    public void testShowCenteredParagraphWithExtras() throws IOException {
        String firstName = "Mister";
        String lastName = "Nine";

        Image img = null;
        try (   InputStream imageResource = getClass().getResourceAsStream("/mkl/testarea/itext7/annotate/Willi-1.jpg") ) {
            ImageData data = ImageDataFactory.create(StreamUtil.inputStreamToArray(imageResource));
            img = new Image(data);
            img.scaleToFit(100f, 100f);
        }

        try (   PdfWriter pdfWriter = new PdfWriter(new File(RESULT_FOLDER, "CenterParagraphWithExtras.pdf"));
                PdfDocument pdfDocument = new PdfDocument(pdfWriter);
                Document document = new Document(pdfDocument)   ) {
            PageSize box = pdfDocument.getDefaultPageSize();

            Paragraph paragraph = new Paragraph("Hello! This PDF is created for "+firstName+" "+lastName);
            paragraph.setWidth(100);
            document.showTextAligned(paragraph, (box.getLeft() + box.getRight()) / 2, (box.getTop() + box.getBottom()) / 2,
                    TextAlignment.CENTER, VerticalAlignment.MIDDLE);

            PdfCanvas pdfCanvas = new PdfCanvas(pdfDocument.getLastPage());
            Rectangle borderRectangle = new Rectangle(box.getLeft() + 5, box.getBottom() + 5, box.getWidth() - 10, box.getHeight() - 10);
            pdfCanvas.setColor(new DeviceRgb(0f, 0f, 0.6f), false);
            pdfCanvas.setLineWidth(3);
            pdfCanvas.rectangle(borderRectangle);
            pdfCanvas.stroke();

            img.setFixedPosition(box.getLeft() + 40, box.getTop() - 150);
            document.add(img);
            img.setFixedPosition(box.getLeft() + 40, box.getBottom() + 50);
            document.add(img);
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/65339560/add-a-text-on-an-existing-pdf-document-by-appending-something-after-the-pdf-cont">
     * Add a text on an existing PDF document by appending something after the PDF content
     * </a>
     * <p>
     * This test adds content to a PDF in append mode for Fratt (the OP
     * of the SO question) to analyze and reverse-engineer.
     * </p>
     */
    @Test
    public void testAddCenteredBorderedParagraph() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("test.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfWriter pdfWriter = new PdfWriter(new File(RESULT_FOLDER, "test-CenterParagraph.pdf"));
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter, new StampingProperties().useAppendMode());
                Document document = new Document(pdfDocument)   ) {
            pdfWriter.setCompressionLevel(0);
            Paragraph paragraph = new Paragraph("Hello! This text is added for Fratt");
            paragraph
                .setWidth(100)
                .setBorder(new SolidBorder(new DeviceRgb(0f, 0f, 0.6f), 3))
                .setRotationAngle(Math.PI / 4);
            Rectangle box = pdfDocument.getFirstPage().getCropBox();
            document.showTextAligned(paragraph,
                (box.getLeft() + box.getRight()) / 2,
                (box.getTop() + box.getBottom()) / 2,
                TextAlignment.CENTER,
                VerticalAlignment.MIDDLE);
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/67601323/add-paragraph-via-itext-layout-document-showtextaligned-on-first-page-only">
     * Add Paragraph via itext.Layout.Document.ShowTextAligned on first Page only
     * </a>
     * <p>
     * In the course of this question it became clear that the code in
     * {@link #testAddCenteredBorderedParagraph()} does not add to the
     * first page but instead to the large. Using a different overload
     * of <code>Document.showTextAligned</code> allows adding to the
     * first page, though.
     * </p>
     */
    @Test
    public void testAddCenteredBorderedParagraphOriginalFile() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("originalFile.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfWriter pdfWriter = new PdfWriter(new File(RESULT_FOLDER, "originalFile-CenterParagraph.pdf"));
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter, new StampingProperties().useAppendMode());
                Document document = new Document(pdfDocument)   ) {
            pdfWriter.setCompressionLevel(0);
            Paragraph paragraph = new Paragraph("Hello! This text is added for Fratt");
            paragraph
                .setWidth(100)
                .setBorder(new SolidBorder(new DeviceRgb(0f, 0f, 0.6f), 3))
                .setRotationAngle(Math.PI / 4);
            Rectangle box = pdfDocument.getFirstPage().getCropBox();
            document.showTextAligned(paragraph,
                (box.getLeft() + box.getRight()) / 2,
                (box.getTop() + box.getBottom()) / 2,
                1,
                TextAlignment.CENTER,
                VerticalAlignment.MIDDLE,
                0);
        }
    }
}
