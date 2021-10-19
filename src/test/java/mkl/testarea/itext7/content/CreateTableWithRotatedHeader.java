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
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.splitting.ISplitCharacters;

/**
 * @author mkl
 */
public class CreateTableWithRotatedHeader {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/50840714/vertical-text-in-table-header">
     * Vertical text in table header?
     * </a>
     * <p>
     * This test shows how to use rotated text in a table header.
     * </p>
     */
    @Test
    public void testCreateTableForUser648026() throws IOException {
        try (   OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "simpleTableUser648026.pdf"));
                PdfWriter writer = new PdfWriter(result);
                PdfDocument pdfDocument = new PdfDocument(writer);
                Document doc = new Document(pdfDocument)   )
        {
            ISplitCharacters noSplit = (text, glyphPos) -> false;

            Table table = new Table(4);

            table.addHeaderCell(new Cell().add(new Paragraph("First Col Header")).setVerticalAlignment(VerticalAlignment.BOTTOM));
            table.addHeaderCell(new Paragraph("Second Column Header").setRotationAngle(Math.PI / 2).setSplitCharacters(noSplit));
            table.addHeaderCell(new Cell().add(new Paragraph("Third Column Header").setRotationAngle(Math.PI / 2).setSplitCharacters(noSplit)).setVerticalAlignment(VerticalAlignment.BOTTOM));
            table.addHeaderCell(new Cell().add(new Paragraph("Fourth Column Header")).setVerticalAlignment(VerticalAlignment.BOTTOM));

            table.addCell("Row 1 Description");
            table.addCell("12");
            table.addCell("15");
            table.addCell("27");

            table.addCell("Row 2 Description");
            table.addCell("25");
            table.addCell("12");
            table.addCell("37");

            table.addCell("Sum");
            table.addCell("37");
            table.addCell("27");
            table.addCell("64");

            doc.add(table);
        }
    }
}
