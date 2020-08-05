package mkl.testarea.itext7.extract;

import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;

public class HiddenText {
    public HiddenText(int page, Matrix imageMatrix, String text, PdfXObject xobject) {
        this.page = page;
        this.imageMatrix = imageMatrix;
        this.text = text;
        this.xobject = xobject;
    }

    public int getPage() {
        return page;
    }

    public Matrix getImageMatrix() {
        return imageMatrix;
    }

    public String getText() {
        return text;
    }

    public PdfXObject getXobject() {
        return xobject;
    }

    final int page;
    final Matrix imageMatrix;
    final String text;
    final PdfXObject xobject;
}
