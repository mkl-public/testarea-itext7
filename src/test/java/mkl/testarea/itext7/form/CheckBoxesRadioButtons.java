package mkl.testarea.itext7.form;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

/**
 * @author mkl
 */
public class CheckBoxesRadioButtons {
    final static File RESULT_FOLDER = new File("target/test-outputs", "form");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/64858117/fill-pdf-check-field-with-itext7">
     * Fill pdf check field with iText7
     * </a>
     * <br/>
     * <a href="https://drive.google.com/file/d/1shZ8z5QDsgzuVNU2QIY9wfUzpTNGNbLi/view?usp=sharing">
     * CheckTest.pdf
     * </a>
     * <p>
     * Indeed, the check boxes in questions actually are check box fields used
     * in a way to work as radio buttons in Adobe Reader. iText only in actual
     * radio button fields by default refrains from creating own appearances.
     * Thus, by default the normal appearances here are replaced by new ones
     * which assume a check box functionality.
     * </p>
     * <p>
     * Explicitly forbidding the generation of new appearances prevents this issue.
     * </p>
     */
    @Test
    public void testSelectCheckTest() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("CheckTest.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfWriter pdfWriter = new PdfWriter(new File(RESULT_FOLDER, "CheckTest-Selected.pdf"));
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter) ) {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDocument, true);
            PdfFormField check = form.getField("Check");
            check.setValue("01", false);
        }
    }

}
