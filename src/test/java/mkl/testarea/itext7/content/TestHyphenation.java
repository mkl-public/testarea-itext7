package mkl.testarea.itext7.content;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.hyphenation.HyphenationConfig;

/**
 * @author mkl
 */
public class TestHyphenation {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/49064491/hyphenationconfig-for-itext7">
     * HyphenationConfig for iText7
     * </a>
     * <p>
     * Cannot reproduce the issue.
     * </p>
     */
    @Test
    public void testLikeFreedom() throws IOException {
        try (   FileOutputStream target = new FileOutputStream(new File(RESULT_FOLDER, "hyphenation-like-Freedom.pdf"));
                PdfWriter pdfWriter = new PdfWriter(target);
                PdfDocument pdfDocument = new PdfDocument(pdfWriter)    ) {
            Document document = new Document(pdfDocument);
            
            Paragraph paragraph = new Paragraph(
                    "If you can distribute the content over different pages you would need to insert a ...");
            paragraph.add("Based on the value of fits, you can decide to change the size of the rectangle or the content.");
            paragraph.add("In this example, area contains the coordinates of the desired rectangle.");
            paragraph.add("One part of a situation , idea, plan etc that has many parts.");
            paragraph.add("It's defined by which horizontal or vertical slice of the world you connect 10000yourself with.");
            HyphenationConfig hyphenationConfig = new HyphenationConfig("en", "US", 2, 2);
            paragraph.setHyphenation(hyphenationConfig);
            document.add(paragraph);
            document.close();
        }
    }
}
