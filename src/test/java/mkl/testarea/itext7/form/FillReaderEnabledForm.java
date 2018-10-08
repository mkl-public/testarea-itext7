package mkl.testarea.itext7.form;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;

/**
 * @author mkl
 */
public class FillReaderEnabledForm {
    final static File RESULT_FOLDER = new File("target/test-outputs", "form");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/47581765/why-are-is-my-form-being-flattened-with-outcalling-the-flattenfields-method">
     * Why are is my form being flattened with outcalling the flattenFields method?
     * </a>
     * <br/>
     * <a href="https://help.adobe.com/en_US/Acrobat/9.0/Samples/interactiveform_enabled.pdf">
     * interactiveform_enabled.pdf
     * </a>
     * <p>
     * The form is Reader-enabled. Thus, one has to stamp it in append mode as seen here.
     * </p>
     */
    @Test
    public void testFillInteractiveFormEnabled() throws IOException {
        try (InputStream resource = getClass().getResourceAsStream("interactiveform_enabled.pdf")) {
            PdfDocument pdf = new PdfDocument(new PdfReader(resource), new PdfWriter(new File(RESULT_FOLDER, "interactiveform_enabled_filled.pdf")), new StampingProperties().useAppendMode());
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);
            Map<String, PdfFormField> m = form.getFormFields();
            for (String s : m.keySet()) {
                if (s.equals("Name_First")) {
                    m.get(s).setValue("Tristan");
                }
                if (s.equals("BACHELORS DEGREE")) {
                    m.get(s).setValue("Off"); // On or Off
                }
                if (s.equals("Sex")) {
                    m.get(s).setValue("FEMALE");
                }
                System.out.println(s);
            }
            pdf.close();
        }
    }
}
