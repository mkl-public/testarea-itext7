package mkl.testarea.itext7.content;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.io.font.FontMetrics;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

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

}
