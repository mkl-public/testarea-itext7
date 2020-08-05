package mkl.testarea.itext7.extract;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.itextpdf.io.source.IRandomAccessSource;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.NoninvertibleTransformException;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.ImageRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextChunkLocation;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.TextChunk;

/**
 * <p>
 * This class inspects page contents and looks for images hiding text.
 * </p>
 * <p>
 * It is in particular designed for detecting PDFs that might be used
 * in hide Shadow Attacks as proposed by the Ruhr Uni Bochum, see
 * https://www.pdf-insecurity.org/signature-shadow/shadow-attacks.html
 * </p>
 * 
 * @author mkl
 */
public class ContentAnalyzer implements Closeable {
    public ContentAnalyzer(IRandomAccessSource byteSource, ReaderProperties properties) throws IOException {
        PdfReader pdfReader = new PdfReader(byteSource, properties);
        pdfReader.setUnethicalReading(true);
        pdfDocument = new PdfDocument(pdfReader);
    }

    @Override
    public void close() {
        pdfDocument.close();
    }

    public Multimap<Integer, HiddenText> findHiddenTexts() {
        Multimap<Integer, HiddenText> hiddenTexts = MultimapBuilder.treeKeys().arrayListValues().build();

        for (int pageNr = 1; pageNr <= pdfDocument.getNumberOfPages(); pageNr++) {
            PdfPage page = pdfDocument.getPage(pageNr);
            Strategy strategy = new Strategy(pageNr);
            PdfTextExtractor.getTextFromPage(page, strategy);
            hiddenTexts.putAll(pageNr, strategy.getHiddenTexts());
        }

        return hiddenTexts;
    }

    class Strategy extends LocationTextExtractionStrategy {
        @SuppressWarnings("unchecked")
        public Strategy(int pageNr) {
            super();
            this.pageNr = pageNr;
            try {
                field = LocationTextExtractionStrategy.class.getDeclaredField("locationalResult");
                field.setAccessible(true);
                locationalResult = (List<TextChunk>) field.get(this);
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException("Failue retrieving LocationTextExtractionStrategy member locationalResult", e);
            }
        }

        @Override
        public void eventOccurred(IEventData data, EventType type) {
            if (type == EventType.RENDER_IMAGE) {
                ImageRenderInfo renderInfo = (ImageRenderInfo) data;
                Matrix imageCtm = renderInfo.getImageCtm();
                AffineTransform inverseCtm = inverse(imageCtm);
                List<TextChunk> notCovered = new ArrayList<TextChunk>(locationalResult.size());
                for (TextChunk chunk : locationalResult) {
                    Point checkPoint = getCheckPoint(chunk.getLocation());
                    Point pullback = inverseCtm.transform(checkPoint, null);
                    if (!isInUnitSquare(pullback))
                        notCovered.add(chunk);
                }
                if (notCovered.size() < locationalResult.size()) {
                    locationalResult.removeAll(notCovered);
                    String text = getResultantText();
                    HiddenText hiddenText = new HiddenText(pageNr, imageCtm, text, renderInfo.getImage());
                    hiddenTexts.add(hiddenText);
                    locationalResult.clear(); // Or not?
                    locationalResult.addAll(notCovered);
                }
            }
            super.eventOccurred(data, type);
        }

        Point getCheckPoint(ITextChunkLocation location) {
            Vector start = location.getStartLocation();
            Vector end = location.getEndLocation();
            return new Point((start.get(Vector.I1) + end.get(Vector.I1)) / 2, (start.get(Vector.I2) + end.get(Vector.I2)) / 2);
        }

        boolean isInUnitSquare(Point point) {
            double x = point.getX();
            double y = point.getY();
            return 0 <= x && x <= 1 && 0 <= y && y <= 1;
        }

        AffineTransform inverse(Matrix ctm) {
            try {
                AffineTransform t = new AffineTransform(
                        ctm.get(Matrix.I11), ctm.get(Matrix.I12),
                        ctm.get(Matrix.I21), ctm.get(Matrix.I22),
                        ctm.get(Matrix.I31), ctm.get(Matrix.I32)
                );
                return t.createInverse();
            } catch (NoninvertibleTransformException e) {
                return null;
            }
        }

        public List<HiddenText> getHiddenTexts() {
            return hiddenTexts;
        }

        final int pageNr;
        final List<HiddenText> hiddenTexts = new ArrayList<HiddenText>();
        final Field field;
        final List<TextChunk> locationalResult;
    }

    final PdfDocument pdfDocument;
}
