package mkl.testarea.itext7.html2pdf;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;

/**
 * @author mkl
 */
public class Landscape {
    final static File RESULT_FOLDER = new File("target/test-outputs", "html2pdf");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/61059881/itext-7-converting-html-to-pdf-fails-when-using-landscape-mode-in-some-cases-t">
     * itext 7: converting HTML to PDF fails when using landscape mode in some cases (test repo link included)
     * </a>
     * <br/>
     * <a href="https://github.com/abrighton/itext-bug/raw/master/TEST.html">
     * TEST.html
     * </a>
     * <p>
     * Indeed, the issue can be reproduced, there are AreaBreak renderers
     * whose draw method is called in case of the landscape layout but not
     * in case of the portrait layout.
     * </p>
     * @see #testLikeAllanLandscape()
     */
    @Test
    public void testLikeAllanPortrait() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("TEST.html") ) {
            String html = new String(StreamUtil.inputStreamToArray(resource));
            OutputStream out = new FileOutputStream(new File(RESULT_FOLDER, "TEST-portrait.pdf"));
            saveAsPdf(out, html, "portrait");
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/61059881/itext-7-converting-html-to-pdf-fails-when-using-landscape-mode-in-some-cases-t">
     * itext 7: converting HTML to PDF fails when using landscape mode in some cases (test repo link included)
     * </a>
     * <br/>
     * <a href="https://github.com/abrighton/itext-bug/raw/master/TEST.html">
     * TEST.html
     * </a>
     * <p>
     * Indeed, the issue can be reproduced, there are AreaBreak renderers
     * whose draw method is called in case of the landscape layout but not
     * in case of the portrait layout.
     * </p>
     * @see #testLikeAllanPortrait()
     */
    @Test
    public void testLikeAllanLandscape() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("TEST.html") ) {
            String html = new String(StreamUtil.inputStreamToArray(resource));
            OutputStream out = new FileOutputStream(new File(RESULT_FOLDER, "TEST-landscape.pdf"));
            saveAsPdf(out, html, "landscape");
        }
    }

    void saveAsPdf(OutputStream out, String html, String orientation) throws IOException {
        PageSize pageSize = orientation.equals("landscape") ? PageSize.LETTER.rotate() : PageSize.LETTER;
        PdfWriter writer  = new PdfWriter(out);
        PdfDocument document = new PdfDocument(writer);
        document.setDefaultPageSize(pageSize);
        HtmlConverter.convertToPdf(new ByteArrayInputStream(html.getBytes()), document);
        out.close();
    }
}
