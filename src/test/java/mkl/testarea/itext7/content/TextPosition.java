package mkl.testarea.itext7.content;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.io.font.FontMetrics;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

/**
 * @author mkl
 */
public class TextPosition {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/60879440/finding-text-ive-added-to-a-pdf-with-itext-7">
     * Finding text I've added to a PDF with iText 7
     * </a>
     * <p>
     * As it turns out, the exact position of text, even if positioned at a
     * "fixed position", depends on both font properties and other settings
     * like margins, padding, leading, ...
     * </p>
     * <p>
     * Thus, if you want to put the text at a really fixed position, use low
     * level {@link PdfCanvas} functionality.
     * </p>
     */
    @Test
    public void testLikePhilM() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new File(RESULT_FOLDER, "TextPositionLikePhilM.pdf")));
        pdfDocument.addNewPage(PageSize.LETTER.rotate());

        Document document = new Document(pdfDocument);

        PdfFont helv = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        Paragraph paragraph = new Paragraph("test string");
        paragraph.setFont(helv);
        paragraph.setFontSize(8);
        paragraph.setFixedPosition(100, 194, 100);
        document.add(paragraph);

        paragraph = new Paragraph("test text");
        paragraph.setFont(helv);
        paragraph.setFontSize(8);
        paragraph.setFixedPosition(250, 194, 100);
        document.add(paragraph);

        for (String string : Arrays.asList("test string", "test text"))
            System.out.printf("'%s' at %s: ascent: %s, descent: %s\n", string, 8, helv.getAscent(string, 8), helv.getDescent(string, 8));

        FontMetrics metrics = helv.getFontProgram().getFontMetrics();
        System.out.printf("Helvetica ascender: %s, descender: %s, box: [ ", metrics.getAscender(), metrics.getDescender());
        for (int i : metrics.getBbox())
            System.out.printf("%s ", i);
        System.out.println("]");

        pdfDocument.addNewPage(PageSize.LETTER.rotate());
        PdfCanvas canvas = new PdfCanvas(pdfDocument, 2);
        canvas.beginText()
              .setFontAndSize(helv, 8)
              .moveText(100, 194)
              .showText("test string")
              .moveText(150, 0)
              .showText("test text")
              .endText();

        document.close();
    }

    /**
     * <a href="https://stackoverflow.com/questions/64972425/what-value-to-use-for-moveup-of-canvas">
     * What value to use for .MoveUp of canvas
     * </a>
     * <p>
     * This test runs the code of the OP optimized to not rely on
     * magic numbers: The offset by 4 observed by the OP actually
     * is required to counteract the {@link Paragraph} default top
     * margin value of 4. Thus, explicitly setting the paragraph
     * top margin to 0 resolves the issue.
     * </p>
     */
    @Test
    public void testForGustav() throws IOException {
        try (
                PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new File(RESULT_FOLDER, "TextPositionForGustav.pdf")));
                Document document = new Document(pdfDocument);
        ) {
            document.setMargins(0, 0, 0, 0);
            Style style = registrationStyle();
            Paragraph paragraph = new Paragraph("Testing 4567X").addStyle(style).setFontSize(40).setMarginTop(0);
            document.add(paragraph);
        }
    }

    /** @see #testForGustav() */
    private static Style registrationStyle() throws IOException {
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        float fontSize = 8F;
        float rightPadding = 3F;
        TextAlignment textAlignment = TextAlignment.RIGHT;
        Color borderColor = ColorConstants.RED;
        Color fillColor = ColorConstants.WHITE;
        float borderWidth = 0.7F;

        Style style = new Style()
            .setFont(font)
            .setFontSize(fontSize)
            .setPaddingRight(rightPadding)
            .setTextAlignment(textAlignment)
            .setBackgroundColor(fillColor)
            .setBorder(new SolidBorder(borderColor, borderWidth));

        return style;
    }
}
