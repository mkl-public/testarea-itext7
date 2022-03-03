package mkl.testarea.itext7.font;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

/**
 * @author mkl
 */
public class ThousandsSeparator {
    final static File RESULT_FOLDER = new File("target/test-outputs", "font");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/71322025/wrong-rendering-of-thousands-separator-with-custom-font-in-itext">
     * Wrong rendering of thousands separator with custom font in itext
     * </a>
     * <br/>
     * <a href="https://www.swisstransfer.com/d/69d4f4c1-cf2b-4a51-a841-493237b3164f">
     * Brown-Bold.otf
     * </a>
     * <p>
     * Cannot reproduce the problem, even in Java. I always get "1.000,00 €" for the "fr-BE" culture.
     * </p>
     */
    @Test
    public void testLikeJohnValdevit() throws IOException {
        try (   InputStream fontResource = getClass().getResourceAsStream("Brown-Bold.otf") ) {
            PdfFont fontTexte = PdfFontFactory.createFont(StreamUtil.inputStreamToArray(fontResource),
                    PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);

            // Initialisation
            ByteArrayOutputStream ms = new ByteArrayOutputStream();

            // Initialize PDF writer
            PdfWriter writer = new PdfWriter(ms);
            writer.setCloseStream(false);

            // Initialize PDF document
            PdfDocument pdfDoc = new PdfDocument(writer);

            // Initialize document
            Document document = new Document(pdfDoc, PageSize.A4).setFont(fontTexte).setFontSize(10);

            // Values
            Locale locale = new Locale("fr", "BE");
            for (int i = 1000; i <= 2000; i += 100)
                document.add(new Paragraph(String.format(locale, "%,d\n", i)));

            // Close document
            document.close();

            Files.write(new File(RESULT_FOLDER, "ThousandsSeparatorLikeJohnValdevit.pdf").toPath(), ms.toByteArray());
        }
    }

}
