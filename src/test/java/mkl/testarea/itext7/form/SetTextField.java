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
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
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
     * But see {@link #testSetFontAndTextToFieldWithManyVisualizations()}.
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

    /**
     * <a href="https://stackoverflow.com/questions/54116803/itext-7-set-text-field-once-even-if-it-exists-multiple-times-with-the-same-nam">
     * iText 7 : Set text field once even if it exists multiple times with the same name
     * </a>
     * <br/>
     * <a href="https://drive.google.com/file/d/16IgO74D4zwbSpO0Di0PympYZhYYPDUz9/view?usp=sharing">
     * itext_multiple_text.pdf
     * </a>
     * <br/>
     * <a href="https://drive.google.com/file/d/1Tqx1ihUkll542ZzfurGqRMse3lCc9X10/view?usp=sharing">
     * Arimo-Regular.ttf
     * </a>
     * <p>
     * Using the <code>setValue</code> overload with additional font font size
     * parameters one can reproduce the issue. Actually only the overload with
     * only a single parameter used in {@link #testSetTextToFieldWithManyVisualizations()}
     * takes multiple widgets into account.
     * </p>
     */
    @Test
    public void testSetFontAndTextToFieldWithManyVisualizations() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("itext_multiple_text.pdf");
                InputStream fontResource = getClass().getResourceAsStream("Arimo-Regular.ttf");
                PdfReader reader = new PdfReader(resource);
                OutputStream outputStream = new FileOutputStream(new File(RESULT_FOLDER, "itext_multiple_text-with-font-and-text.pdf"));) {
            PdfDocument pdfDocument = new PdfDocument(reader, new PdfWriter(outputStream));
            PdfAcroForm acroForm = PdfAcroForm.getAcroForm(pdfDocument, false);

            PdfFont font = PdfFontFactory.createFont(StreamUtil.inputStreamToArray(fontResource), PdfEncodings.IDENTITY_H);
            PdfFormField textField = acroForm.getField("test");
            textField.setValue("שלום", font, 11.0f);

            acroForm.flattenFields();

            pdfDocument.close();
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/54116803/itext-7-set-text-field-once-even-if-it-exists-multiple-times-with-the-same-nam">
     * iText 7 : Set text field once even if it exists multiple times with the same name
     * </a>
     * <br/>
     * <a href="https://drive.google.com/file/d/16IgO74D4zwbSpO0Di0PympYZhYYPDUz9/view?usp=sharing">
     * itext_multiple_text.pdf
     * </a>
     * <br/>
     * <a href="https://drive.google.com/file/d/1Tqx1ihUkll542ZzfurGqRMse3lCc9X10/view?usp=sharing">
     * Arimo-Regular.ttf
     * </a>
     * <p>
     * A work-around for the issue in {@link #testSetFontAndTextToFieldWithManyVisualizations()}
     * - by setting font and font size as the text field properties
     * one can again use the single parameter <code>setValue</code>
     * overload. Apparently, though, the character order is inverted.
     * </p>
     */
    @Test
    public void testSetFontAndTextToFieldWithManyVisualizationsWorkAround() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("itext_multiple_text.pdf");
                InputStream fontResource = getClass().getResourceAsStream("Arimo-Regular.ttf");
                PdfReader reader = new PdfReader(resource);
                OutputStream outputStream = new FileOutputStream(new File(RESULT_FOLDER, "itext_multiple_text-with-font-and-text-workaround.pdf"));) {
            PdfDocument pdfDocument = new PdfDocument(reader, new PdfWriter(outputStream));
            PdfAcroForm acroForm = PdfAcroForm.getAcroForm(pdfDocument, false);

            PdfFont font = PdfFontFactory.createFont(StreamUtil.inputStreamToArray(fontResource), PdfEncodings.IDENTITY_H);
            PdfFormField textField = acroForm.getField("test");
            textField.setFont(font);
            textField.setFontSize(11f);
            textField.setValue("שלום");

            acroForm.flattenFields();

            pdfDocument.close();
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/75738984/why-itext7-did-not-find-the-form">
     * Why itext7 did not find the form
     * </a>
     * <br/>
     * <a href="https://drive.google.com/file/d/1892phrksdbMhRANsMWKzNWFt_m_1aO8r/view?usp=sharing">
     * Т-1.pdf
     * </a>
     * <p>
     * Cannot reproduce the issue, the field is found and filled as desired.
     * </p>
     */
    @Test
    public void testSetFieldLikeEkatarinaElInT1() throws IOException {
        try (
            InputStream resource = getClass().getResourceAsStream("Т-1.pdf");
            PdfReader reader = new PdfReader(resource);
            PdfWriter writer = new PdfWriter(new File(RESULT_FOLDER, "Т-1-filledIn.pdf"));
            PdfDocument document = new PdfDocument(reader, writer);
        ) {
            PdfAcroForm form = PdfAcroForm.getAcroForm(document, true);
            PdfFormField nameField = form.getField("employer_full_name");
            nameField.setValue("The employer's full name");
        }
    }
}
