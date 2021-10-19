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
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.kernel.pdf.navigation.PdfStringDestination;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.AreaBreakType;

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

    /**
     * <a href="https://stackoverflow.com/questions/69557142/how-do-i-create-a-forward-link-to-a-specific-page-which-does-not-yet-exists-when">
     * How do I create a forward link to a specific page which does not yet exists when I generate the link
     * </a>
     * <p>
     * This test demonstrates how to create a link to a not yet created page
     * by means of named destinations.
     * </p>
     */
    @Test
    public void testCreateForwardLink() throws IOException {
        try (   FileOutputStream fos = new FileOutputStream(new File(RESULT_FOLDER, "forwardLink.pdf"));
                PdfDocument pdfDocument = new PdfDocument(new PdfWriter(fos));
                Document document = new Document(pdfDocument)   )
        {
            String destinationName = "MyForwardDestination";

            for (int page = 1; page <= 50; page++) {
                document.add(new Paragraph().setFontSize(100).add(String.valueOf(page)));
                switch (page) {
                case 1:
                    document.add(new Paragraph(new Link("Click here for a forward jump", new PdfStringDestination(destinationName)).setFontSize(20)));
                    break;
                case 42:
                    pdfDocument.addNamedDestination(destinationName, PdfExplicitDestination.createFit(pdfDocument.getLastPage()).getPdfObject());
                    break;
                }
                if (page < 50)
                    document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            }
        }
    }
}
