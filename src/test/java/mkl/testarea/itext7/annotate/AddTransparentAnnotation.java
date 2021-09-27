package mkl.testarea.itext7.annotate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfSquareAnnotation;

/**
 * @author mkl
 */
public class AddTransparentAnnotation {
    final static File RESULT_FOLDER = new File("target/test-outputs", "annotate");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/69285245/itext7-unable-to-set-the-the-opacity-for-squareannotation-interior-color">
     * iText7 - unable to set the the Opacity for SquareAnnotation Interior color
     * </a>
     * <p>
     * The problem in the OP's code turns out to be the opacity values,
     * 25 and 30; changing them to 0.25 and 0.30 fixed the issue.
     * </p>
     */
    @Test
    public void testAddTransparentSquareLikeDangerousDev() throws IOException {
        try (   InputStream resourceStream = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf");
                OutputStream outputStream = new FileOutputStream(new File(RESULT_FOLDER, "TransparentSquareLikeDangerousDev.pdf"));
                PdfReader pdfReader = new PdfReader(resourceStream);
                PdfWriter pdfWriter = new PdfWriter(outputStream);
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter);    ) {

            PdfSquareAnnotation annotation = addAnnotation(500, 500, 100, 200);
            pdfDocument.getPage(1).addAnnotation(annotation);
        }
    }

    /**
     * Original code of the OP with the opacity values replaced,
     * 25 -> 0.25f and 30 -> 0.30f.
     * 
     * @see #testAddTransparentSquareLikeDangerousDev()
     */
    private PdfSquareAnnotation addAnnotation(float rectHeight, float rectWidth, float x, float y)
    {
        Rectangle rect = new Rectangle(x, y, rectWidth, rectHeight);
        PdfSquareAnnotation squareAnnotation = new PdfSquareAnnotation(rect);
        squareAnnotation.setColor(ColorConstants.GREEN);
        squareAnnotation.setTitle(new PdfString("This is the title"));
        squareAnnotation.setContents("This is the contents of the annotation. bla bla..");
        squareAnnotation.setNonStrokingOpacity(0.25f);
        squareAnnotation.setOpacity(new PdfNumber(0.30f));
        squareAnnotation.setInteriorColor(new float[] { (float)0.294, (float)0.552, (float)0.968 });

        return squareAnnotation;
    }
}
