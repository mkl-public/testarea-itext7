package mkl.testarea.itext7.content;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.OverflowPropertyValue;
import com.itextpdf.layout.property.Property;

/**
 * @author mkl
 */
public class RenderDivOverflowHidden {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/49601728/itext-7-how-can-i-allow-overflow-in-a-div">
     * iText 7: How can I allow overflow in a Div?
     * </a>
     * <p>
     * This method tests the {@link OverflowHiddenDivRenderer} which
     * renderer renders a Div so that it appears cut off at its HEIGHT
     * property; in particular, if the cut off occurs in the middle of
     * a line, that line is still visible above that cut off line.
     * </p>
     * <p>
     * Beware, I may well have forgotten some special cases. I think in
     * particular of problems in context with rotated content (which in
     * super.getOccupiedAreaBBox() is of influence) or area breaks (I don't
     * set a next renderer in OverflowHiddenDivRenderer with an adapted
     * height).
     * </p>
     */
    @Test
    public void testOverflowHiddenDivRenderer() throws IOException {
        try (   FileOutputStream target = new FileOutputStream(new File(RESULT_FOLDER, "divOverflowHidden.pdf"));
                PdfWriter pdfWriter = new PdfWriter(target);
                PdfDocument pdfDocument = new PdfDocument(pdfWriter);
                Document document = new Document(pdfDocument)   ) {
            for (int height = 100; height < 150; height += 5) {
                Div div = new Div();
                div.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.HIDDEN);
                div.add(new Paragraph(height + " Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet."));
                div.setHeight(height);
//                div.setBorder(new SolidBorder(1));
                div.setNextRenderer(new OverflowHiddenDivRenderer(div));
                document.add(div);
            }
        }
    }

}
