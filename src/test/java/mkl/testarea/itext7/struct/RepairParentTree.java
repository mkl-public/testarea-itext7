package mkl.testarea.itext7.struct;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;

/**
 * @author mkl
 */
public class RepairParentTree {
    final static File RESULT_FOLDER = new File("target/test-outputs", "struct");

    @BeforeClass
    public static void setUp() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/57591441/find-tag-from-selection-is-not-working-in-tagged-pdf">
     * “Find Tag from Selection” is not working in tagged pdf?
     * </a>
     * <br/>
     * <a href="https://drive.google.com/file/d/1aD1HGQsEXOovpfWdf7JRNwJhP7tX6pmy/view?usp=sharing">
     * mathpdf.pdf
     * </a>
     * <p>
     * An unnecessary layer of structure elements is referenced from
     * the parent tree instead of the actual structure hierarchy parents
     * directly. This test method removes that layer.
     * </p>
     * <p>
     * As we want to manipulate the parent tree manually, we keep iText
     * from manipulating it, too.
     * </p>
     */
    @Test
    public void testRepairMathpdf() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("mathpdf.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfWriter pdfWriter = new PdfWriter(new File(RESULT_FOLDER, "mathpdf-repaired.pdf"));
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter) {
                    @Override
                    protected void open(PdfVersion newPdfVersion) {
                        super.open(newPdfVersion);
                        structTreeRoot = null;
                    }
                }) {
            PdfDictionary catalog = pdfDocument.getCatalog().getPdfObject();
            PdfDictionary structTreeRoot = catalog.getAsDictionary(PdfName.StructTreeRoot);
            PdfDictionary parentTree = structTreeRoot.getAsDictionary(PdfName.ParentTree);
            PdfArray nums = parentTree.getAsArray(PdfName.Nums);
            for (int i = 0; i < nums.size(); i++) {
                PdfObject pdfObject = nums.get(i, true);
                if (pdfObject.isArray()) {
                    PdfArray array = (PdfArray) pdfObject;
                    PdfArray newArray = new PdfArray();
                    for (PdfObject elem : array) {
                        PdfObject direct = elem.isIndirectReference() ? ((PdfIndirectReference)elem).getRefersTo(true) : elem;
                        if (direct.isDictionary()) {
                            PdfDictionary dict = (PdfDictionary) direct;
                            PdfDictionary p = dict.getAsDictionary(PdfName.P);
                            if (p != null) {
                                newArray.add(p);
                                continue;
                            }
                        }
                        newArray.add(elem);
                    }
                    nums.set(i, newArray);
                }
            }
            nums.setModified();
        }
    }

}
