package mkl.testarea.itext7.form;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

public class Flatten {
    final static File RESULT_FOLDER = new File("target/test-outputs", "form");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/76730992/com-itextpdf-kernel-pdfexception-structureelement-shall-contain-parent-object-e">
     * com.itextpdf.kernel.PdfException: StructureElement shall contain parent object exception is thrown when form.flattenFields() method is called in iText
     * </a>
     * <br/>
     * <a href="https://filetransfer.io/data-package/rIjG3Zfu#link">
     * NORMAL_LTR.pdf
     * </a>
     * <p>
     * Just like the OP reported, flattening his example document fails. The cause is
     * that there are errors in the structure tree of the document, some form field widgets
     * have associated structure elements which are not in the structure tree. iText form
     * flattening stumbles in the attempt to properly update the structure tree when removing
     * those widgets.
     * </p>
     * <p>
     * If you make iText believe that the document is not tagged (like in the text method here),
     * it doesn't attempt that structure tree update and, consequentially, flattens the form
     * without further ado.
     * </p>
     */
    @Test
    public void testFlattenNormalLtr() throws IOException {
        try(
            InputStream resource = getClass().getResourceAsStream("NORMAL_LTR.pdf");
            PdfReader pdfReader = new PdfReader(resource);
            PdfWriter pdfWriter = new PdfWriter("target/test-outputs/form/NORMAL_LTR-flattened.pdf");
            PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter) {
                @Override
                public boolean isTagged() {
                    return false;
                }
                
            }
        ){
            PdfAcroForm pdfAcroForm = PdfAcroForm.getAcroForm(pdfDocument, true);
            pdfAcroForm.flattenFields();
        }
    }

}
