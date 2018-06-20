package mkl.testarea.itext7.extract;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.LineSegment;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;

/**
 * @author mkl
 */
public class ExtractPositionalText {
    final static File RESULT_FOLDER = new File("target/test-outputs", "extract");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/50894662/how-to-find-text-position-and-boundary-in-itext-7">
     * How to find text position and boundary in iText 7
     * </a>
     * <br/>
     * <a href="https://github.com/voidmagic/pdf-parser/blob/master/data/chinese.pdf">
     * chinese.pdf
     * </a>
     * <p>
     * As it turns out the cause is a bug in iText's CMap loading. When loading
     * a font encoding CMap, iText stores the data inversely in a Map, i.e. akin
     * to <code>map.put(value, key)</code>. Unfortunately the encoding of the
     * footer font at hand is <b>GBK-EUC-H</b> which is not injective. Thus, during
     * CMap loading some entries are overridden and the matching character codes
     * cannot be resolved, causing extracted text containing replacement characters
     * unnecessarily and extracted locations ignore these characters. 
     * </p>
     */
    @Test
    public void testMarkTextLikeQianWang() throws IOException {
        File destFileName = new File(RESULT_FOLDER, "chinese-markedText.pdf");
        try (   InputStream resource = getClass().getResourceAsStream("chinese.pdf")    ) {
            PdfDocument pdfDoc = new PdfDocument(new PdfReader(resource), new PdfWriter(destFileName));
            SimplePositionalTextEventListener listener = new SimplePositionalTextEventListener();
            new PdfCanvasProcessor(listener).processPageContent(pdfDoc.getFirstPage());
            List<SimpleTextWithRectangle> result = listener.getResultantTextWithPosition();

            int R = 0, G = 0, B = 0;
            for(SimpleTextWithRectangle textWithRectangle: result) {
                R += 40; R = R % 256;
                G += 20; G = G % 256;
                B += 80; B = B % 256;
                PdfCanvas canvas = new PdfCanvas(pdfDoc.getPage(1));
                canvas.setStrokeColor(new DeviceRgb(R, G, B));
                canvas.rectangle(textWithRectangle.getRectangle());
                canvas.stroke();

                System.out.printf("%s - %d %d %d\n", textWithRectangle.getText(), R, G, B);
            }

            pdfDoc.close();
        }
    }

    static class SimpleTextWithRectangle {
        private Rectangle rectangle;
        private String text;

        public SimpleTextWithRectangle(Rectangle rectangle, String text) {
            this.rectangle = rectangle;
            this.text = text;
        }

        public Rectangle getRectangle() {
            return rectangle;
        }

        public String getText() {
            return text;
        }
    }

    static class SimplePositionalTextEventListener implements IEventListener {
        private List<SimpleTextWithRectangle> textWithRectangleList = new ArrayList<>();

        private void renderText(TextRenderInfo renderInfo) {
            if (renderInfo.getText().trim().length() == 0)
                return;
            LineSegment ascent = renderInfo.getAscentLine();
            LineSegment descent = renderInfo.getDescentLine();

            float initX = descent.getStartPoint().get(0);
            float initY = descent.getStartPoint().get(1);
            float endX = ascent.getEndPoint().get(0);
            float endY = ascent.getEndPoint().get(1);

            Rectangle rectangle = new Rectangle(initX, initY, endX - initX, endY - initY);

            SimpleTextWithRectangle textWithRectangle = new SimpleTextWithRectangle(rectangle, renderInfo.getText());
            textWithRectangleList.add(textWithRectangle);
        }

        public List<SimpleTextWithRectangle> getResultantTextWithPosition() {
            return textWithRectangleList;
        }

        @Override
        public void eventOccurred(IEventData data, EventType type) {
            renderText((TextRenderInfo) data);
        }

        @Override
        public Set<EventType> getSupportedEvents() {
            return Collections.unmodifiableSet(new LinkedHashSet<>(Collections.singletonList(EventType.RENDER_TEXT)));
        }
    }
}
