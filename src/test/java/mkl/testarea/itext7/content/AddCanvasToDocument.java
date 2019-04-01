package mkl.testarea.itext7.content;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;

/**
 * @author mkl
 */
public class AddCanvasToDocument {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/55208465/add-canvas-between-line-in-itext-7-without-overlapping-page">
     * Add canvas between line in itext 7 Without Overlapping Page
     * </a>
     * <br/>
     * Graph.png, a screenshot of a graph from page 18 of
     * <a href="https://www.pwc.com/gx/en/mining/pdf/mine-2016.pdf">
     * mine-2016.pdf
     * </a>
     * <p>
     * This test shows how one can add a something one added
     * content to via a {@link Canvas} to a {@link Document}.
     * </p>
     */
    @Test
    public void testAddCanvasForRuslan() throws IOException {
        File DEST = new File(RESULT_FOLDER, "AddCanvasForRuslan.pdf");

        String text = "Until recently, increasing dividend yields grabbed the headlines. However, increasing\n" + 
                "yields were actually more a reflection of the market capitalisation challenge than of the\n" + 
                "fortunes of mining shareholders. The yields mask a complete u-turn from boom-time\n" + 
                "dividend policies. More companies have now announced clear percentages of profit\n" + 
                "distribution policies. The big story today is the abandonment of progressive dividends\n" + 
                "by the majors, confirming that no miner was immune from a sustained commodity\n" + 
                "cycle downturn, however diversified their portfolio. \n" +
                "\ngraph_add\n\n" +
                "Shareholders were not fully rewarded for the high commodity prices and huge\n" + 
                "profits experienced in the boom, as management ploughed cash and profits into\n" + 
                "bigger and more marginal assets. During those times, production was the main\n" + 
                "game and shareholders were rewarded through soaring stock prices. However,\n" + 
                "this investment proposition relied on prices remaining high. ";

        final Image img;
        try (InputStream imageResource = getClass().getResourceAsStream("Graph.png")) {
            ImageData data = ImageDataFactory.create(StreamUtil.inputStreamToArray(imageResource));
            img = new Image(data);
        }

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(DEST));
        PageSize ps = PageSize.A4;;
        Document doc = new Document(pdfDoc, ps);

        Rectangle effectivePageSize = doc.getPageEffectiveArea(ps);
        img.scaleToFit(effectivePageSize.getWidth(), effectivePageSize.getHeight());
        PdfFormXObject pdfFormXObject = new PdfFormXObject(new Rectangle(img.getImageScaledWidth(), img.getImageScaledHeight()));
        PdfCanvas pdfCanvas = new PdfCanvas(pdfFormXObject, pdfDoc);
        try (Canvas canvas = new Canvas(pdfCanvas, pdfDoc, pdfFormXObject.getBBox().toRectangle())) {
            canvas.add(img);
        }

        BufferedReader br = new BufferedReader(new StringReader(text));
        String line;
        while ((line = br.readLine()) != null) {
            if("graph_add".equals(line)) {
                doc.add(new Image(pdfFormXObject));
            } else {
                doc.add(new Paragraph(line));
            }
        }
        doc.close();
    }

}
