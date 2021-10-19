package mkl.testarea.itext7.content;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

/**
 * @author mkl
 */
public class CreateCyrillicText {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/47677271/while-parsing-pdf-with-itext7-chars-move-on-fixed-interval-with-freeset-font">
     * While parsing pdf with iText7 chars move on fixed interval (with Freeset font)
     * </a>
     * <br/>
     * <a href="http://allfont.ru/download/freeset/">
     * freeset.ttf
     * </a>
     * <p>
     * This code reproduces the issue. Apparently iText 7 does understand
     * the font well enough to select the needed subset and reference the
     * correct glyphs from the content but it has problems building an
     * appropriate <b>ToUnicode</b> map. This is not a general problem,
     * though, as the parallel test with Arial shows.  
     * </p>
     */
    @Test
    public void testCreateTextWithFreeSet() throws IOException {
        PdfFont freeSet;
        PdfFont arial = PdfFontFactory.createFont(Files.readAllBytes(new File("c:\\Windows\\Fonts\\arial.ttf").toPath()), PdfEncodings.IDENTITY_H, EmbeddingStrategy.PREFER_EMBEDDED);
        try (   InputStream resource = getClass().getResourceAsStream("freeset.ttf")    ) {
            freeSet = PdfFontFactory.createFont(StreamUtil.inputStreamToArray(resource), PdfEncodings.IDENTITY_H, EmbeddingStrategy.PREFER_EMBEDDED);
        }
        try (   OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "cyrillicTextFreeSet.pdf"));
                PdfWriter writer = new PdfWriter(result);
                PdfDocument pdfDocument = new PdfDocument(writer);
                Document doc = new Document(pdfDocument)    ) {
            doc.add(new Paragraph("Фамилия").setFont(arial));
            doc.add(new Paragraph("Фамилия").setFont(freeSet));
        }
    }

}
