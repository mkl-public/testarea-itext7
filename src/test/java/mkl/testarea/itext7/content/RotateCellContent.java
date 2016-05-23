// $Id$
package mkl.testarea.itext7.content;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

/**
 * @author mklink
 */
public class RotateCellContent
{
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="http://stackoverflow.com/questions/37382957/rotating-text-inside-pdfpcell-itextpdf-c-sharp">
     * Rotating text inside PDFPCell iTextPDF C#
     * </a>
     * <p>
     * This sample shows how to rotate cell contents.
     * </p>
     */
    @Test
    public void testSimpleRotatedCellContent() throws IOException
    {
        try (   FileOutputStream target = new FileOutputStream(new File(RESULT_FOLDER, "simpleRotatedCellContent.pdf"));
                PdfWriter pdfWriter = new PdfWriter(target);
                PdfDocument pdfDocument = new PdfDocument(pdfWriter)    )
        {
            Document document = new Document(pdfDocument);
            Table table = new Table(new float[]{150, 150});
            Cell cell = new Cell();
            cell.setRotationAngle(40 * Math.PI / 180.0)
                .add(new Paragraph("Test Watermark Text Copyright Test Only"))
                .setBorder(Border.NO_BORDER);
            table.addCell(cell);
            table.addCell(cell.clone(true));
            table.addCell(cell.clone(true));
            table.addCell(cell.clone(true));
            document.add(table);
        }
    }
}
