package mkl.testarea.itext7.font;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;

/**
 * @author mkl
 */
public class CheckForCharacter {
    /**
     * <a href="https://stackoverflow.com/questions/53546009/how-to-find-out-what-letter-is-not-in-the-font">
     * How to find out what letter is not in the font?
     * </a>
     * <br/>
     * <a href="https://dropmefiles.com/8lFiM">
     * ArtScript.ttf
     * </a> as "ArtScript-a-emptied.ttf"
     * <br/>
     * and with 'a' actually removed as "ArtScript-a-removed.ttf"
     * <p>
     * This test checks how {@link PdfFont} deals with missing characters
     * in a font. The OPs original font program "ArtScript-a-emptied.ttf"
     * merely has cleaned the glyph for 'a' void of any drawing instructions,
     * the manipulated font program "ArtScript-a-removed.ttf" has actually
     * removed the glyph for 'a'. {@link PdfFont#containsGlyph(int)} reports
     * accordingly.
     * </p>
     */
    @Test
    public void testCheckForMissingAInArtScript() throws IOException {
        try (   InputStream aEmptiedStream = getClass().getResourceAsStream("ArtScript-a-emptied.ttf")) {
            PdfFont font = PdfFontFactory.createFont(StreamUtil.inputStreamToArray(aEmptiedStream), PdfEncodings.IDENTITY_H, true);
            System.out.println("ArtScript-a-emptied.ttf");
            System.out.println("font_without_a " + font.getWidth("a",17));
            System.out.println(font.containsGlyph('a'));
        }

        try (   InputStream aRemovedStream = getClass().getResourceAsStream("ArtScript-a-removed.ttf")) {
            PdfFont font = PdfFontFactory.createFont(StreamUtil.inputStreamToArray(aRemovedStream), PdfEncodings.IDENTITY_H, true);
            System.out.println("ArtScript-a-removed.ttf");
            System.out.println("font_without_a " + font.getWidth("a",17));
            System.out.println(font.containsGlyph('a'));
        }
    }

}
