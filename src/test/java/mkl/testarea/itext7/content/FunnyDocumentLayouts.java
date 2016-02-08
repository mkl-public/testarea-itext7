package mkl.testarea.itext7.content;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

/**
 * Experiments with document layout structures.
 * 
 * @author mkl
 */
public class FunnyDocumentLayouts
{
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * Render into a diagonal sequence of 100x100 rectangles.
     */
    @Test
    public void testManyColummns() throws FileNotFoundException
    {
        FileOutputStream fos = new FileOutputStream(new File(RESULT_FOLDER, "funnyDocumentLayouts.pdf"));
        PdfWriter writer = new PdfWriter(fos);
        
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);
        doc.setRenderer(new MultiColumnDocumentRenderer(doc, new Rectangle(50, 700, 100, 100),
                new Rectangle(150, 600, 100, 100), new Rectangle(250, 500, 100, 100),
                new Rectangle(350, 400, 100, 100), new Rectangle(450, 300, 100, 100)));

        StringBuilder builder = new StringBuilder();
        builder.append(0);
        for (int i = 1; i < 1000; i++)
        {
            builder.append(' ').append(i);
        }
        
        doc.add(new Paragraph(builder.toString()).setBackgroundColor(Color.YELLOW));
        doc.close();
    }

}
