package mkl.testarea.itext7.html2pdf;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;

/**
 * @author mkl
 */
public class CorrectSizes {
    final static File RESULT_FOLDER = new File("target/test-outputs", "html2pdf");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/57476868/why-are-more-pixel-used-in-resulting-pdf-than-in-source-html">
     * Why are more Pixel used in resulting PDF than in source html?
     * </a>
     * <p>
     * This essentially is the OP's code. Indeed, the difference in
     * image width can be reproduced: iText renders at 72dpi, browsers
     * at 96dpi. As the web page does not have a "background-size" set,
     * this difference actually is ok: In this case the intrinsic size
     * is used but the spec does not tell how to determine this
     * "intrinsic size".
     * </p>
     */
    @Test
    public void testHtml2PdfVnda2019() throws IOException {
        //eine spezielle URL heraus picken
        String kongressURL = "https://www.egms.de/dynamic/de/meetings/vnda2019/index.htm";

        Document doc = Jsoup.connect(kongressURL).get();

        System.out.println("-----Titel: "+ doc.title());
        Element content = doc.child(0);
        content.getElementById("navigation_language").remove();
        content.getElementById("navigation").remove();
        content.getElementsByAttributeValue("href", "/static/css/gms-framework.css").first().remove();
        content.getElementsByClass("hidden_navigation").first().remove();
        content.getElementById("page").before(content.getElementById("header"));
        content.getElementsByTag("script").remove();
        content.getElementById("owner_links_container").attr("style", "border-top:10px solid #060");
//        content.getElementById("owner_description").attr("style", "background-size:120px 100px");

        Files.write(new File(RESULT_FOLDER, "content.html").toPath(), content.html().getBytes());

        ConverterProperties properties = new ConverterProperties();
        properties.setBaseUri(kongressURL);
        PdfWriter writer = new PdfWriter(new File(RESULT_FOLDER, "content.pdf"));
        HtmlConverter.convertToPdf(content.html(), new PdfDocument(writer), properties);
    }

}
