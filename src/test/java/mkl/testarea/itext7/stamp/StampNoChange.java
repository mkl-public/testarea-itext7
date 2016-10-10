package mkl.testarea.itext7.stamp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.tagging.IPdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;

/**
 * @author mkl
 */
public class StampNoChange
{
    final static File RESULT_FOLDER = new File("target/test-outputs", "stamp");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="http://stackoverflow.com/questions/39937615/itext-7-pdfdictionary-released-prematurely">
     * iText 7: PdfDictionary released prematurely
     * </a>
     * <br/>
     * <a href="https://mega.nz/#!bh90VI4S!bbjRXOm3EAc2BA-OQPsJKZ5Sei9Xfu9JQl72M0YnYg0">
     * prematureReleaseExample.pdf
     * </a>
     * <p>
     * Indeed, this simple no-change stamping fails with a {@link NullPointerException}
     * due to a structure tree node already been flushed. The cause is that a few nodes
     * appear multiple times in the structure tree of the document at hand.
     * </p>
     * <p>
     * iText can be hardened against this problem by adding an `isFlushed` test to
     * {@link PdfStructTreeRoot#flushAllKids(IPdfStructElem)}:
     * </p>
     * <pre>
     * private void flushAllKids(IPdfStructElem elem) {
     *     for (IPdfStructElem kid : elem.getKids()) {
     *         if (kid instanceof PdfStructElem) {
     *             if (!((PdfStructElem) kid).isFlushed())
     *             {
     *                 flushAllKids(kid);
     *                 ((PdfStructElem) kid).flush();
     *             }
     *         }
     *     }
     * }
     * </pre>
     */
    @Test
    public void testPrematureReleaseExample() throws IOException
    {
        File target = new File(RESULT_FOLDER, "prematureReleaseExample-asis.pdf");
        try (   InputStream resource = getClass().getResourceAsStream("prematureReleaseExample.pdf");
                OutputStream dest = new FileOutputStream(target))
        {
            PdfReader reader = new PdfReader(resource);
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument doc = new PdfDocument(reader, writer);
            doc.close();
        }
    }
}
