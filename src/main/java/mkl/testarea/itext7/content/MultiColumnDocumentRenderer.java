package mkl.testarea.itext7.content;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.RootLayoutArea;
import com.itextpdf.layout.renderer.DocumentRenderer;

/**
 * A simple {@link DocumentRenderer} rendering into the given rectangles.
 * 
 * @author mkl
 */
public class MultiColumnDocumentRenderer extends DocumentRenderer
{
    public MultiColumnDocumentRenderer(Document doc, Rectangle... rectangles)
    {
        super(doc);
        this.rectangles = rectangles;
    }

    int nextAreaNumber = 0;
    int currentPageNumber;

    @Override
    public LayoutArea updateCurrentArea(LayoutResult overflowResult) {
        if (nextAreaNumber % rectangles.length == 0)
        {
            currentPageNumber = super.updateCurrentArea(overflowResult).getPageNumber();
        }
        return (currentArea = new RootLayoutArea(currentPageNumber, rectangles[nextAreaNumber++ % rectangles.length]));
    }

    final Rectangle[] rectangles;
}
