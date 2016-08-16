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
import com.itextpdf.layout.element.Table;

/**
 * @author mkl
 */
public class TablesToCopyAndPasteFrom
{
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="http://stackoverflow.com/questions/38784227/how-to-copy-the-texts-from-a-generated-pdf-table">
     * how to copy the texts from a generated pdf table?
     * </a>
     * <p>
     * Setting the <code>PdfDocument</code> attribute <code>Tagged</code> improves table extraction.
     * But some glitches remain.
     * </p>
     */
    @Test
    public void testCreateCaosTables() throws IOException
    {
        try (   FileOutputStream target = new FileOutputStream(new File(RESULT_FOLDER, "caosTables.pdf"));
                PdfWriter pdfWriter = new PdfWriter(target);
                PdfDocument pdfDocument = new PdfDocument(pdfWriter)    )
        {
            pdfDocument.setTagged();
            @SuppressWarnings("resource")
            Document document = new Document(pdfDocument);

            float[] tableWidth = {75, 75, 75};

            Table table1 = new Table(tableWidth);
            table1.addHeaderCell("head \n1");
            table1.addHeaderCell("head \n2");
            table1.addHeaderCell("head \n3");
            table1.addCell("column 1");
            table1.addCell("column 2");
            table1.addCell("column 3");

            Table table2 = new Table(tableWidth);
            table2.addHeaderCell("head 1");
            table2.addHeaderCell("head 2");
            table2.addHeaderCell("head 3");
            table2.addCell("column 1");
            table2.addCell("column 2");
            table2.addCell("column 3");

            document.add(table1);
            document.add(new Paragraph("\n"));
            document.add(table2);
        }
    }
}
