// $Id$
package mkl.testarea.itext7.annotate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

/**
 * @author mkl
 */
public class CreateLink
{
    final static File RESULT_FOLDER = new File("target/test-outputs", "annotate");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="http://stackoverflow.com/questions/34408764/create-local-link-in-rotated-pdfpcell-in-itextsharp">
     * Create local link in rotated PdfPCell in iTextSharp
     * </a>
     * <p>
     * This is the equivalent Java code for iText 7 of the C# code for iTextSharp 5
     * in the question. Indeed, this code also gives rise to the broken result.
     * </p>
     */
    @Test
    public void testCreateLocalLinkInRotatedCell() throws IOException
    {
        try (   FileOutputStream fos = new FileOutputStream(new File(RESULT_FOLDER, "linkInRotatedCell.pdf"));
                PdfDocument pdfDocument = new PdfDocument(new PdfWriter(fos)))
        {
            Document document = new Document(pdfDocument);
            Table table = new Table(2);

            Link chunk = new Link("Click here", PdfAction.createURI("http://itextpdf.com/"));
            table.addCell(new Cell().add(new Paragraph().add(chunk)).setRotationAngle(Math.PI / 2));

            chunk = new Link("Click here 2", PdfAction.createURI("http://itextpdf.com/"));
            table.addCell(new Paragraph().add(chunk));

            document.add(table);
            document.close();
        }
    }
}
