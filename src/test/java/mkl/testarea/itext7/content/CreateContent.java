package mkl.testarea.itext7.content;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

/**
 * @author mkl
 */
public class CreateContent {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/49894744/itext-7-1-add-paragraph-to-document">
     * Itext 7.1 add paragraph to document
     * </a>
     * <p>
     * Cannot reproduce the issue. Might be a problem of the OP's license file.
     * </p>
     */
    @Test
    public void testCreateParagraphInNewDocument() throws IOException {
        try (   OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "paragraphInNewDocument.pdf"));
                PdfWriter writer = new PdfWriter(result);
                PdfDocument pdfDocument = new PdfDocument(writer);
                Document doc = new Document(pdfDocument)    ) {
            doc.add(new Paragraph("Test"));
        }
    }
}
