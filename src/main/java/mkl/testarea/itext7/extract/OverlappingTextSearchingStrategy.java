package mkl.testarea.itext7.extract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.ImageRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.data.PathRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;

/**
 * <a href="https://stackoverflow.com/questions/46362605/how-to-find-whether-a-pdf-file-has-overlapping-text-or-not-using-c-sharp">
 * How to find whether a PDF file has overlapping text or not, using c#
 * </a>
 * <p>
 * This {@link IEventListener} attempts to check whether overlapping
 * text as defined by the OP exists on the page parsed through it.
 * </p>
 * <p>
 * The OP defined "overlapping text" as text whose bounding box overlaps
 * the bounding box of other text or of some image. Neither vector graphics
 * nor annotations are to be considered. 
 * </p>
 * <p>
 * This is a prove-of-concept only. In particular it works best by far for
 * horizontal and vertical text and images as the bounding boxes are used 
 * with only horizontal and vertical edges. 
 * </p>
 * <p>
 * Kerning and tightly set text might result in false positives. To make
 * this less likely, text bounding boxes are slightly shrunk before they
 * are stored for later comparison. The margins used may have to be fine-
 * tuned for the task at hand.
 * </p>
 * 
 * @author mkl
 */
public class OverlappingTextSearchingStrategy implements IEventListener {

    static List<Vector> UNIT_SQUARE_CORNERS = Arrays.asList(new Vector(0,0,1), new Vector(1,0,1), new Vector(1,1,1), new Vector(0,1,1));

    Set<Rectangle> imageRectangles = new HashSet<>();
    Set<Rectangle> textRectangles = new HashSet<>();

    @Override
    public void eventOccurred(IEventData data, EventType type) {
        if (data instanceof ImageRenderInfo) {
            ImageRenderInfo imageData = (ImageRenderInfo) data;
            Matrix ctm = imageData.getImageCtm();
            List<Rectangle> cornerRectangles = new ArrayList<>(UNIT_SQUARE_CORNERS.size());
            for (Vector unitCorner : UNIT_SQUARE_CORNERS) {
                Vector corner = unitCorner.cross(ctm);
                cornerRectangles.add(new Rectangle(corner.get(Vector.I1), corner.get(Vector.I2), 0, 0));
            }
            Rectangle boundingBox = Rectangle.getCommonRectangle(cornerRectangles.toArray(new Rectangle[cornerRectangles.size()]));
            logger.info(String.format("Adding image bounding rectangle %s.", boundingBox));
            imageRectangles.add(boundingBox);
        } else if (data instanceof TextRenderInfo) {
            TextRenderInfo textData = (TextRenderInfo) data;
            Rectangle ascentRectangle = textData.getAscentLine().getBoundingRectangle();
            Rectangle descentRectangle = textData.getDescentLine().getBoundingRectangle();
            Rectangle boundingBox = Rectangle.getCommonRectangle(ascentRectangle, descentRectangle);
            if (boundingBox.getHeight() == 0 || boundingBox.getWidth() == 0)
                logger.info(String.format("Ignoring empty text bounding rectangle %s for '%s'.", boundingBox, textData.getText()));
            else {
                logger.info(String.format("Adding text bounding rectangle %s for '%s' with 0.5 margins.", boundingBox, textData.getText()));
                textRectangles.add(boundingBox.applyMargins(0.5f, 0.5f, 0.5f, 0.5f, false));
            }
        } else if (data instanceof PathRenderInfo) {
            // TODO
        } else if (data != null) {
            logger.fine(String.format("Ignored %s event, class %s.", type, data.getClass().getSimpleName()));
        } else {
            logger.fine(String.format("Ignored %s event with null data.", type));
        }
    }

    @Override
    public Set<EventType> getSupportedEvents() {
        // Support all events
        return null;
    }

    public boolean foundOverlappingText() {
        boolean result = false;
        List<Rectangle> textRectangleList = new ArrayList<>(textRectangles);

        while (!textRectangleList.isEmpty())
        {
            Rectangle testRectangle = textRectangleList.remove(textRectangleList.size() - 1);

            for (Rectangle rectangle : textRectangleList) {
                if (intersect(testRectangle, rectangle)) {
                    logger.info(String.format("Found text intersecting text with bounding boxes %s at %s,%s and %s at %s,%s.",
                            testRectangle, testRectangle.getX(), testRectangle.getY(), rectangle, rectangle.getX(), rectangle.getY()));
                    result = true;// if only the fact counts, do instead: return true
                }
            }
            for (Rectangle rectangle : imageRectangles) {
                if (intersect(testRectangle, rectangle)) {
                    logger.info(String.format("Found text intersecting image with bounding boxes %s at %s,%s and %s at %s,%s.", 
                            testRectangle, testRectangle.getX(), testRectangle.getY(), rectangle, rectangle.getX(), rectangle.getY()));
                    result = true;// if only the fact counts, do instead: return true
                }
            }
        }

        return result;
    }

    boolean intersect(Rectangle a, Rectangle b) {
        return intersect(a.getLeft(), a.getRight(), b.getLeft(), b.getRight()) &&
                intersect(a.getBottom(), a.getTop(), b.getBottom(), b.getTop());
    }

    boolean intersect(float start1, float end1, float start2, float end2) {
        if (start1 < start2)
            return start2 <= end1;
        else
            return start1 <= end2;
    }

    Logger logger = Logger.getLogger(OverlappingTextSearchingStrategy.class.getName());
}
