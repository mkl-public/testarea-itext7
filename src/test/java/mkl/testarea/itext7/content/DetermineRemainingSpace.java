package mkl.testarea.itext7.content;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

/**
 * @author mkl
 */
public class DetermineRemainingSpace {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/57575085/remaining-space-on-pdf-page">
     * Remaining Space on PDF Page
     * </a>
     * <p>
     * This test shows how to determine the yet unused page main area,
     * given that the standard document renderer is used in a standard
     * way.
     * </p>
     */
    @Test
    public void testForStandardDocumentRenderer() throws IOException {
        try (   PdfWriter pdfWriter = new PdfWriter(new File(RESULT_FOLDER, "DetermineRemainingSpace.pdf"));
                PdfDocument pdfDocument = new PdfDocument(pdfWriter);
                Document document = new Document(pdfDocument)   ) {
            for (int i = 0; i < 30; i++) {
                Rectangle currentBox = document.getRenderer().getCurrentArea().getBBox();
                String current = String.format(Locale.ROOT, "%3.2f×%3.2f from (%3.2f, %3.2f) to (%3.2f, %3.2f)", currentBox.getWidth(), currentBox.getHeight(),
                        currentBox.getLeft(), currentBox.getBottom(), currentBox.getRight(), currentBox.getTop());
                document.add(new Paragraph(String.format("%02d, previously available %s", i, current)));
            }
        }
    }

}
