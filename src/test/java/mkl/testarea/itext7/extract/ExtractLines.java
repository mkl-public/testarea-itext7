package mkl.testarea.itext7.extract;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.geom.IShape;
import com.itextpdf.kernel.geom.Line;
import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.geom.Subpath;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.ImageRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.data.PathRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.CharacterRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.itextpdf.layout.element.Paragraph;

/**
 * @author mkl
 */
public class ExtractLines {
    final static File RESULT_FOLDER = new File("target/test-outputs", "extract");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/66564370/fetch-rectangle-line-segmentsco-ordinates-from-pdf-using-itextpdf">
     * Fetch rectangle (line segments)co ordinates from pdf using itextpdf
     * </a>
     * <p>
     * Indeed, the {@link FreeSpaceFinder} only processes three sides
     * of each rectangle. The cause is that iText reports rectangles
     * as subpaths containing three explicit lines (bottom, right, top)
     * with their Closed property set implying the fourth line (left).
     * The {@link FreeSpaceFinder} on the other hand only considers the
     * explicit subpath segments.
     * </p>
     */
    @Test
    public void testExtractLikeDigvijaySinghChauhan() throws IOException {
        byte[] examplePdf = createPdfLikeDigvijaySinghChauhan();
        Files.write(new File(RESULT_FOLDER, "PdfLikeDigvijaySinghChauhan.pdf").toPath(), examplePdf, StandardOpenOption.CREATE);

        try (   PdfDocument pdfDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(examplePdf)))  ) {
            for (int page = 1; page <= pdfDocument.getNumberOfPages(); page++) {
                FreeSpaceFinder freeSpaceFinder = new FreeSpaceFinder(pdfDocument.getPage(page));
            }
        }
    }

    /** @see #testExtractLikeDigvijaySinghChauhan() */
    byte[] createPdfLikeDigvijaySinghChauhan() throws IOException {
        try (   ByteArrayOutputStream baos = new ByteArrayOutputStream()    ) {
            try (   PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos))  ) {
                PageSize ps = pdfDocument.getDefaultPageSize();
                Paragraph p = new Paragraph("This is the text added in the rectangle.");
                PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
                //Rectangle rect = new Rectangle(ps.getWidth() - 90, ps.getHeight() - 100, 50, 50);
                Rectangle rect1 = new Rectangle(50, 50, 100, 50);
                Rectangle rect2 = new Rectangle(150, 50, 100, 50);
                canvas.rectangle(rect1);
                canvas.rectangle(rect2);
                canvas.stroke();
            }
            return baos.toByteArray();
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/46240121/itext-image-on-pdf-only-if-the-position-is-blank">
     * iText : image on PDF only if the position is blank
     * </a>
     * <p>
     * This event listener determines a collection of occupied areas.
     * </p>
     * @author Joris Schellekens
     */
    class FreeSpaceFinder implements IEventListener {
        private Collection<Rectangle> areas = new HashSet<>();

        public FreeSpaceFinder(PdfPage page) {
            areas.clear();
            PdfCanvasProcessor processor = new PdfCanvasProcessor(this);
            processor.processPageContent(page);
        }

        public Collection<Rectangle> getOccupiedAreas(){
            return areas;
        }

        @Override
        public void eventOccurred(IEventData iEventData, EventType eventType) {
            if(eventType == EventType.RENDER_TEXT)
                processText((TextRenderInfo) iEventData);
            else if(eventType == EventType.RENDER_PATH)
                processPath((PathRenderInfo) iEventData);
            else if(eventType == EventType.RENDER_IMAGE)
                processImage((ImageRenderInfo) iEventData);
        }

        private void processText(TextRenderInfo info) {
            for(TextRenderInfo characterRenderInfo : info.getCharacterRenderInfos()) {
                com.itextpdf.kernel.geom.Rectangle charBoundingBox = new CharacterRenderInfo(characterRenderInfo).getBoundingBox();
                areas.add(new Rectangle(    (int) charBoundingBox.getX(),
                                            (int) charBoundingBox.getY(),
                                            (int) charBoundingBox.getWidth(),
                                            (int) charBoundingBox.getHeight()));
            }
        }

        private void processPath(PathRenderInfo info) {
            for(Subpath subpath : info.getPath().getSubpaths()) {
                for(IShape segment : subpath.getSegments()) {
                    if(segment instanceof Line) {
                        processLine(info, (Line) segment);
                    }
                }
            }
        }

        private float[] applyMtx(Matrix m, float[] point) {
            Matrix m2 = m.multiply(new Matrix(point[0], point[1]));
            return new float[]{m2.get(Matrix.I31), m2.get(Matrix.I32)};
        }

        private void processLine(PathRenderInfo info, Line shape) {

            float[] p0 = applyMtx(info.getCtm(), new float[]{(float) shape.getBasePoints().get(0).getX(), (float) shape.getBasePoints().get(0).getY()});
            int x0 = (int) p0[0];
            int y0 = (int) p0[1];

            float[] p1 = applyMtx(info.getCtm(), new float[]{(float) shape.getBasePoints().get(1).getX(), (float) shape.getBasePoints().get(1).getY()});
            int x1 = (int) p1[0];
            int y1 = (int) p1[1];

            int w = java.lang.Math.abs(x0 - x1);
            int h = java.lang.Math.abs(y0 - y1);

            areas.add(new Rectangle(java.lang.Math.min(x0,x1), java.lang.Math.min(y0,y1), java.lang.Math.max(w, 1), java.lang.Math.max(h, 1)));
        }

        private void processImage(ImageRenderInfo info) {
            // #TODO
        }

        @Override
        public Set<EventType> getSupportedEvents() {
            return null;
        }
    }
}
