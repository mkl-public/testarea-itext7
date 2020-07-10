package mkl.testarea.itext7.stamp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.property.TextAlignment;

/**
 * @author mkl
 */
public class StampInAppendMode {
    final static File RESULT_FOLDER = new File("target/test-outputs", "stamp");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/62823389/itext7-java-does-not-render-elements-in-appendmode-in-pdf-generated-in-libreof">
     * IText7 (Java) does not render elements in appendMode in PDF generated in LibreOffice
     * </a>
     * <br/>
     * <a href="https://drive.google.com/file/d/1TJvH9uVmCSbjUUhX5CYZ3vnKSoepkTUC/view?usp=sharing">
     * Google Drive.pdf
     * </a>
     * <p>
     * Test drawing text in center of first page. Works fine.
     * </p>
     */
    @Test
    public void testStampWithAppendGoogleDrive() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("Google Drive.pdf");
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "Google Drive-stampedWithAppend.pdf"))   ) {
            testStamp(resource, result, true);
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/62823389/itext7-java-does-not-render-elements-in-appendmode-in-pdf-generated-in-libreof">
     * IText7 (Java) does not render elements in appendMode in PDF generated in LibreOffice
     * </a>
     * <br/>
     * <a href="https://drive.google.com/file/d/1TJvH9uVmCSbjUUhX5CYZ3vnKSoepkTUC/view?usp=sharing">
     * libre-office.pdf
     * </a>
     * <p>
     * Test drawing text in center of first page. Works fine.
     * </p>
     */
    @Test
    public void testStampWithAppendLibreOffice() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("libre-office.pdf");
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "libre-office-stampedWithAppend.pdf"))   ) {
            testStamp(resource, result, true);
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/62823389/itext7-java-does-not-render-elements-in-appendmode-in-pdf-generated-in-libreof">
     * IText7 (Java) does not render elements in appendMode in PDF generated in LibreOffice
     * </a>
     * <br/>
     * <a href="https://drive.google.com/file/d/1TJvH9uVmCSbjUUhX5CYZ3vnKSoepkTUC/view?usp=sharing">
     * libre-office-pdfa.pdf
     * </a>
     * <p>
     * Test drawing text in center of first page. Works fine.
     * </p>
     */
    @Test
    public void testStampWithAppendLibreOfficePdfa() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("libre-office-pdfa.pdf");
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "libre-office-pdfa-stampedWithAppend.pdf"))   ) {
            testStamp(resource, result, true);
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/62823389/itext7-java-does-not-render-elements-in-appendmode-in-pdf-generated-in-libreof">
     * IText7 (Java) does not render elements in appendMode in PDF generated in LibreOffice
     * </a>
     * <br/>
     * <a href="https://drive.google.com/file/d/1TJvH9uVmCSbjUUhX5CYZ3vnKSoepkTUC/view?usp=sharing">
     * Microsoft Word.pdf
     * </a>
     * <p>
     * Test drawing text in center of first page. Works fine.
     * </p>
     */
    @Test
    public void testStampWithAppendMicrosoftWord() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("Microsoft Word.pdf");
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "Microsoft Word-stampedWithAppend.pdf"))   ) {
            testStamp(resource, result, true);
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/62823389/itext7-java-does-not-render-elements-in-appendmode-in-pdf-generated-in-libreof">
     * IText7 (Java) does not render elements in appendMode in PDF generated in LibreOffice
     * </a>
     * <br/>
     * <a href="https://drive.google.com/file/d/1TJvH9uVmCSbjUUhX5CYZ3vnKSoepkTUC/view?usp=sharing">
     * Microsoft Word PDFA.pdf
     * </a>
     * <p>
     * Test drawing text in center of first page. Works fine.
     * </p>
     */
    @Test
    public void testStampWithAppendMicrosoftWordPDFA() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("Microsoft Word PDFA.pdf");
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "Microsoft Word PDFA-stampedWithAppend.pdf"))   ) {
            testStamp(resource, result, true);
        }
    }

    public void testStamp(InputStream input, OutputStream output, boolean append) throws IOException {
        try (   PdfDocument pdf = new PdfDocument(new PdfReader(input), new PdfWriter(output), append ? new StampingProperties().useAppendMode() : new StampingProperties())    ) {
            PdfPage page = pdf.getFirstPage();
            Rectangle cropBox = page.getCropBox();
            try (   Canvas canvas = new Canvas(page, cropBox) ) {
                canvas.setFont(PdfFontFactory.createFont());
                canvas.setFontSize(25);
                canvas.showTextAligned("Test",
                        (cropBox.getLeft() + cropBox.getRight()) / 2f,
                        (cropBox.getBottom() + cropBox.getTop()) / 2f,
                        TextAlignment.CENTER);
            }
        }
    }
}
