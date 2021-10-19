package mkl.testarea.itext7.content;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.DivRenderer;

/**
 * <a href="https://stackoverflow.com/questions/49601728/itext-7-how-can-i-allow-overflow-in-a-div">
 * iText 7: How can I allow overflow in a Div?
 * </a>
 * <p>
 * This renderer renders a Div so that it appears cut off at its HEIGHT
 * property; in particular, if the cut off occurs in the middle of a line,
 * that line is still visible above that cut off line.
 * </p>
 * <p>
 * Beware, I may well have forgotten some special cases. I think in
 * particular of problems in context with rotated content (which in
 * super.getOccupiedAreaBBox() is of influence) or area breaks (I don't
 * set a next renderer in OverflowHiddenDivRenderer with an adapted
 * height).
 * </p>
 * 
 * @author mkl
 */
public class OverflowHiddenDivRenderer extends DivRenderer {
    public OverflowHiddenDivRenderer(Div modelElement) {
        super(modelElement);
    }

    @Override
    public Rectangle getOccupiedAreaBBox() {
        Rectangle rectangle = super.getOccupiedAreaBBox();
        if (height != null) {
            if (rectangle.getHeight() > height.getValue()) {
                rectangle.moveUp(rectangle.getHeight() - height.getValue()).setHeight(height.getValue());
            }
        }
        return rectangle;
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        height = getPropertyAsUnitValue(Property.HEIGHT);
        deleteProperty(Property.HEIGHT);
        LayoutResult layoutResult = super.layout(layoutContext);
        LayoutArea layoutArea = layoutResult.getOccupiedArea();
        if (layoutArea != null) {
            layoutArea.setBBox(getOccupiedAreaBBox());
        }
        return layoutResult;
    }

    UnitValue height;
}
