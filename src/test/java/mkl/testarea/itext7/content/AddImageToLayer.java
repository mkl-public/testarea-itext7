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
import com.itextpdf.kernel.pdf.layer.PdfLayer;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;

public class AddImageToLayer {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/52926448/adding-image-layers-to-a-pdf-using-itext-5-or-7">
     * Adding image layers to a pdf using iText 5 or 7
     * </a>
     * <p>
     * This test shows how to add images in a layer, once via a
     * {@link Canvas} to allow iText style layout'ing and once
     * directly.
     * </p>
     */
    @Test
    public void testAddLikeIan() throws IOException {
        try (   PdfWriter writer = new PdfWriter(new File(RESULT_FOLDER, "addImageToLayerLikeIan.pdf"));
                PdfDocument pdfDoc = new PdfDocument(writer);
                Document document = new Document(pdfDoc);
                InputStream imageResource = getClass().getResourceAsStream("/mkl/testarea/itext7/annotate/Willi-1.jpg")) {
            ImageData data = ImageDataFactory.create(StreamUtil.inputStreamToArray(imageResource));
            Image img = new Image(data);

            PdfLayer pdflayer = new PdfLayer("main layer", pdfDoc);
            pdflayer.setOn(true); 

            // using a Canvas, to allow iText layout'ing the image
            PdfCanvas pdfCanvas = new PdfCanvas(pdfDoc.addNewPage());
            try (   Canvas canvas = new Canvas(pdfCanvas, document.getPageEffectiveArea(pdfDoc.getDefaultPageSize()))   ) {
                canvas.add(new Paragraph("This image is added using a Canvas:"));
                pdfCanvas.beginLayer(pdflayer);
                canvas.add(img);
                pdfCanvas.endLayer();
                canvas.add(new Paragraph("And this image is added immediately:"));
            }

            // or directly 
            pdfCanvas.beginLayer(pdflayer);
            pdfCanvas.addImageAt(data, 100, 100, false);
            pdfCanvas.endLayer();
        }
    }
}
