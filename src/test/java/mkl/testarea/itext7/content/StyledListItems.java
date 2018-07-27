package mkl.testarea.itext7.content;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;

/**
 * @author mkl
 */
public class StyledListItems {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/51529747/how-to-add-text-with-different-weights-to-a-single-listitem-using-itext-list">
     * How to add text with different weights to a single ListItem using iText List
     * </a>
     * <p>
     * This test shows how to create a {@link ListItem} with differently styled parts.
     * </p>
     */
    @Test
    public void testAddMultiStyledListItems() throws IOException {
        try (   FileOutputStream target = new FileOutputStream(new File(RESULT_FOLDER, "multiStyledListItem.pdf"));
                PdfWriter pdfWriter = new PdfWriter(target);
                PdfDocument pdfDocument = new PdfDocument(pdfWriter);
                Document document = new Document(pdfDocument))
        {
            List qaList = new List();

            ListItem item = new ListItem();
            Paragraph paragraph = new Paragraph();
            paragraph.add(new Text("Question 1? ").setBold())
                     .add(new Text("Answer 1"))
                     .setMarginTop(0).setMarginBottom(0);
            item.add(paragraph);
            qaList.add(item);
            item = new ListItem();
            paragraph = new Paragraph();
            paragraph.add(new Text("Question 2? ").setBold())
                     .add(new Text("Answer 2"))
                     .setMarginTop(0).setMarginBottom(0);
            item.add(paragraph);
            qaList.add(item);

            document.add(qaList);
        }
    }

}
