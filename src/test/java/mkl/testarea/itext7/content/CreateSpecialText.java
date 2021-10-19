package mkl.testarea.itext7.content;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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
import com.itextpdf.layout.element.Text;

/**
 * @author mkl
 */
public class CreateSpecialText {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/48559524/a-special-character-u9d28%e9%b4%a8-in-sourcehanseriftc-light-cannot-show-in-pdf">
     * a special character “\u9d28”(鴨) in SourceHanSerifTC-Light cannot show in PDF
     * </a>
     * <br/>
     * <a href="https://www.moedict.tw/fonts/truetype/SourceHanSerifTC/">
     * SourceHanSerifTC-Light.otf, SourceHanSerifTC-Regular.otf 
     * </a>
     * <p>
     * Indeed, the issue can be reproduced for the font in question.
     * </p>
     */
    @Test
    public void testUnicode9d28SourceHanSerifTCLight() throws IOException {
        try (InputStream resource = getClass().getResourceAsStream("SourceHanSerifTC-Light.otf")) {
            PdfWriter dest = new PdfWriter(new File(RESULT_FOLDER, "Unicode9d28SourceHanSerifTC-Light.pdf"));
            PdfDocument pdf = new PdfDocument(dest);
            Document document = new Document(pdf);
            PdfFont font = PdfFontFactory.createFont(StreamUtil.inputStreamToArray(resource), PdfEncodings.IDENTITY_H,
                    EmbeddingStrategy.PREFER_NOT_EMBEDDED);
            Paragraph p = new Paragraph();
            p.setFont(font);
            p.add(new Text("\u9d28"));
            document.add(p);
            document.close();
        }
        try (InputStream resource = getClass().getResourceAsStream("SourceHanSerifTC-Light.otf")) {
            PdfWriter dest = new PdfWriter(new File(RESULT_FOLDER, "Unicode9d289d30SourceHanSerifTC-Light.pdf"));
            PdfDocument pdf = new PdfDocument(dest);
            Document document = new Document(pdf);
            PdfFont font = PdfFontFactory.createFont(StreamUtil.inputStreamToArray(resource), PdfEncodings.IDENTITY_H,
                    EmbeddingStrategy.PREFER_NOT_EMBEDDED);
            Paragraph p = new Paragraph();
            p.setFont(font);
            p.add(new Text("\u9d28"));
            p.add(new Text("\u9d30"));
            document.add(p);
            document.close();
        }
        try (InputStream resource = getClass().getResourceAsStream("SourceHanSerifTC-Regular.otf")) {
            PdfWriter dest = new PdfWriter(new File(RESULT_FOLDER, "Unicode9d28SourceHanSerifTC-Regular.pdf"));
            PdfDocument pdf = new PdfDocument(dest);
            Document document = new Document(pdf);
            PdfFont font = PdfFontFactory.createFont(StreamUtil.inputStreamToArray(resource), PdfEncodings.IDENTITY_H,
                    EmbeddingStrategy.PREFER_NOT_EMBEDDED);
            Paragraph p = new Paragraph();
            p.setFont(font);
            p.add(new Text("\u9d28"));
            document.add(p);
            document.close();
        }
    }

}
