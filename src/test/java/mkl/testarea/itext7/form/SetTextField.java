package mkl.testarea.itext7.form;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

/**
 * @author mkl
 *
 */
public class SetTextField {
    final static File RESULT_FOLDER = new File("target/test-outputs", "form");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/54116803/itext-7-set-text-field-once-even-if-it-exists-multiple-times-with-the-same-nam">
     * iText 7 : Set text field once even if it exists multiple times with the same name
     * </a>
     * <br/>
     * <a href="https://drive.google.com/file/d/16IgO74D4zwbSpO0Di0PympYZhYYPDUz9/view?usp=sharing">
     * itext_multiple_text.pdf
     * </a>
     * <p>
     * Cannot reproduce the issue, neither with 7.1.4 nor 7.1.5-SNAPSHOT.
     * </p>
     */
    @Test
    public void testSetTextToFieldWithManyVisualizations() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("itext_multiple_text.pdf");
                PdfReader reader = new PdfReader(resource);
                OutputStream outputStream = new FileOutputStream(new File(RESULT_FOLDER, "itext_multiple_text-with-text.pdf"));) {
            PdfDocument pdfDocument = new PdfDocument(reader, new PdfWriter(outputStream));
            PdfAcroForm acroForm = PdfAcroForm.getAcroForm(pdfDocument, false);

            PdfFormField testField = acroForm.getField("test");
            testField.setValue("My test text...");

            acroForm.flattenFields();

            pdfDocument.close();
        }
    }

}
