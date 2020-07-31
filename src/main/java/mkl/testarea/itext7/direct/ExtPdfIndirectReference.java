package mkl.testarea.itext7.direct;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;

/**
 * <p>
 * This extended PdfIndirectReference variant additionally stores a
 * revision number and also makes some formerly package protected
 * methods public.
 * </p>
 * <p>
 * First an foremost it is a helper class of the utility class
 * {@link ObjectStructureAnalyzer}.
 * </p>
 * 
 * @author mkl
 */
public class ExtPdfIndirectReference extends PdfIndirectReference {

    protected ExtPdfIndirectReference(PdfDocument doc, int objNr, int genNr, int revNr, long offset) {
        super(doc, objNr, genNr, offset);
        this.revNr = revNr;
    }

    public void setObjStreamNumber(int objectStreamNumber) {
        this.objectStreamNumber = objectStreamNumber;
    }

    public int getRevNr() {
        return revNr;
    }

    public void setRevNr(int revNr) {
        this.revNr = revNr;
    }

    protected int revNr;
}
