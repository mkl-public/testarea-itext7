package mkl.testarea.itext7.merge;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.Path;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.geom.Subpath;
import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.ImageRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.data.PathRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;

/**
 * <a href="https://stackoverflow.com/questions/56522616/itext-7-add-source-pdf-content-to-destination-pdf">
 * IText 7 + Add source PDF content to destination PDF
 * </a>
 * <p>
 * This is a port of the iText5 test area render listener
 * <code>MarginFinder</code>. It is here used to determine
 * the area of the OP's source PDF page with content.
 * </p>
 * 
 * @author mkl
 */
public class MarginFinder implements IEventListener {
    public Rectangle getBoundingBox() {
        return boundingBox != null ? boundingBox.clone() : null;
    }

    @Override
    public void eventOccurred(IEventData data, EventType type) {
        if (data instanceof ImageRenderInfo) {
            ImageRenderInfo imageData = (ImageRenderInfo) data;
            Matrix ctm = imageData.getImageCtm();
            for (Vector unitCorner : UNIT_SQUARE_CORNERS) {
                Vector corner = unitCorner.cross(ctm);
                addToBoundingBox(new Rectangle(corner.get(Vector.I1), corner.get(Vector.I2), 0, 0));
            }
        } else if (data instanceof TextRenderInfo) {
            TextRenderInfo textRenderInfo = (TextRenderInfo) data;
            addToBoundingBox(textRenderInfo.getAscentLine().getBoundingRectangle());
            addToBoundingBox(textRenderInfo.getDescentLine().getBoundingRectangle());
        } else if (data instanceof PathRenderInfo) {
            PathRenderInfo renderInfo = (PathRenderInfo) data;
            if (renderInfo.getOperation() != PathRenderInfo.NO_OP)
            {
                Matrix ctm = renderInfo.getCtm();
                Path path = renderInfo.getPath();
                for (Subpath subpath : path.getSubpaths())
                {
                    for (Point point2d : subpath.getPiecewiseLinearApproximation())
                    {
                        Vector vector = new Vector((float)point2d.getX(), (float)point2d.getY(), 1);
                        vector = vector.cross(ctm);
                        addToBoundingBox(new Rectangle(vector.get(Vector.I1), vector.get(Vector.I2), 0, 0));
                    }
                }
            }
        } else if (data != null) {
            logger.fine(String.format("Ignored %s event, class %s.", type, data.getClass().getSimpleName()));
        } else {
            logger.fine(String.format("Ignored %s event with null data.", type));
        }
    }

    @Override
    public Set<EventType> getSupportedEvents() {
        return null;
    }

    void addToBoundingBox(Rectangle rectangle) {
        if (boundingBox == null)
            boundingBox = rectangle.clone();
        else
            boundingBox = Rectangle.getCommonRectangle(boundingBox, rectangle);
    }

    Rectangle boundingBox = null;
    Logger logger = Logger.getLogger(MarginFinder.class.getName());
    static List<Vector> UNIT_SQUARE_CORNERS = Arrays.asList(new Vector(0,0,1), new Vector(1,0,1), new Vector(1,1,1), new Vector(0,1,1));
}
