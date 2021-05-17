package mkl.testarea.itext7.content;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

/**
 * @author mkl
 */
public class AddImageWithMask {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/67562635/how-to-fill-a-shape-with-bitmap-or-mask-a-bitmap-with-shape-in-itext-7">
     * How to fill a shape with bitmap or mask a bitmap with shape in iText 7?
     * </a>
     * <p>
     * This test demonstrates how to _fill a shape with a bitmap_.
     * </p>
     */
    @Test
    public void test() throws IOException {
        try (   PdfWriter writer = new PdfWriter(new File(RESULT_FOLDER, "addImageInShape.pdf"));
                PdfDocument pdfDoc = new PdfDocument(writer);
                InputStream imageResource = getClass().getResourceAsStream("/mkl/testarea/itext7/annotate/Willi-1.jpg")) {
            ImageData data = ImageDataFactory.create(StreamUtil.inputStreamToArray(imageResource));

            PdfCanvas pdfCanvas = new PdfCanvas(pdfDoc.addNewPage());
            pdfCanvas.saveState()
                     .moveTo(100, 100)
                     .lineTo(300, 200)
                     .lineTo(400, 400)
                     .lineTo(200, 300)
                     .closePath()
                     .eoClip()
                     .endPath();
            pdfCanvas.addImageAt(data, 100, 100, false);
            pdfCanvas.restoreState();
        }
    }

}
