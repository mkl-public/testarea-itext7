package mkl.testarea.itext7.content;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.wmf.WmfImageData;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;

/**
 * @author mkl
 */
public class AddWmfImage {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/51844584/adding-and-rendering-this-wmf-as-a-vector-graphic-in-a-new-pdf-using-itext7">
     * Adding and rendering this WMF as a vector graphic in a new PDF using iText7
     * </a>
     * <br/>
     * <a href="http://s000.tinyupload.com/index.php?file_id=12631255103057077641">
     * Image.wmf
     * </a>
     * <p>
     * Rewriting the OP's code for streaming resource access like here
     * actually showed up another issue: The {@link WmfImageData} constructor
     * accepting a <code>byte[]</code> throws a NPE because it tries to
     * determine the file type using its URL which doesn't exist.
     * </p>
     * <p>
     * After fixing the issue above and running the code, one can indeed
     * witness an issue in the WMF import or in the given WMF file.
     * </p>
     */
    @Test
    public void testAddAlexandrusImage() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("Image.wmf");
                PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new File(RESULT_FOLDER, "AlexandrusImage.pdf")));
                Document document = new Document(pdfDocument)) {
            PdfPage page = pdfDocument.addNewPage();
            PdfFormXObject xObject = new PdfFormXObject(new WmfImageData(StreamUtil.inputStreamToArray(resource)), pdfDocument);
            Image image = new Image(xObject);
            image.setFixedPosition(1, 0, 0, 100);
            image.scaleToFit(30, 30);
            document.add(image);
        }
    }

}
