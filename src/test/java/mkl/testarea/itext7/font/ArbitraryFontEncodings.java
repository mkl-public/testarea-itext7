package mkl.testarea.itext7.font;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

/**
 * @author mkl
 */
public class ArbitraryFontEncodings {
    final static File RESULT_FOLDER = new File("target/test-outputs", "font");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * arslanbenzer answer to <a href="https://stackoverflow.com/a/57805863/1729265">
     * Unicode characters in iText PDF
     * </a>
     * <p>
     * Indeed, as long as the encoding is known via the JRE,
     * one can use arbitrary encoding names in
     * {@link PdfFontFactory#createFont(FontProgram, String)}.
     * There must be Adobe names for the characters in question,
     * though.
     * </p>
     */
    @Test
    public void testLikeArslanbenzer() throws IOException {
        try (   PdfWriter writer = new PdfWriter(new File(RESULT_FOLDER, "ArbitraryFontEncodingsLikeArslanbenzer.pdf"));
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf)) {
            FontProgram fontProgram = FontProgramFactory.createFont( ) ;
            PdfFont font = PdfFontFactory.createFont( fontProgram, "Cp1254" ) ;
            document.setFont(font);

            document.add(new Paragraph("agf bavaulszkvbt"));
        }
    }

}
