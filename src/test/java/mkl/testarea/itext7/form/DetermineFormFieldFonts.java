package mkl.testarea.itext7.form;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;

import org.junit.Test;

import com.google.common.base.Strings;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;

/**
 * @author mkl
 */
public class DetermineFormFieldFonts {
    /**
     * <a href="https://stackoverflow.com/questions/54455849/getting-form-field-font-information-in-itext7">
     * Getting form field font information in itext7
     * </a>
     * <br/>
     * TextFieldsWithFonts.pdf, a PDF with several text fields with different fonts
     * <p>
     * This test shows how to determine form field font
     * information. This actually uses an iText method
     * for doing just that but as the method is protected,
     * a small trick must be used.
     * </p>
     */
    @Test
    public void test() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("TextFieldsWithFonts.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfDocument pdfDocument = new PdfDocument(pdfReader)    ) {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDocument, false);
            for (Entry<String, PdfFormField> entry : form.getFormFields().entrySet()) {
                String fieldName = entry.getKey();
                PdfFormField field = entry.getValue();
                System.out.printf("%s - %s\n", fieldName, field.getFont());

                PdfFormFieldExt extField = new PdfFormFieldExt(field.getPdfObject());
                Object[] fontAndSize = extField.getFontAndSize(field.getWidgets().get(0).getNormalAppearanceObject());
                PdfFont font = (PdfFont) fontAndSize[0];
                Float size = (Float) fontAndSize[1];
                PdfName resourceName = (PdfName) fontAndSize[2];
                System.out.printf("%s - %s - %s - %s\n", Strings.repeat(" ", fieldName.length()),
                        font.getFontProgram().getFontNames(), size, resourceName);
            }
        }
    }

    class PdfFormFieldExt extends PdfFormField {
        public PdfFormFieldExt(PdfDictionary pdfObject) {
            super(pdfObject);
        }

        public Object[] getFontAndSize(PdfDictionary asNormal) throws IOException {
            return super.getFontAndSize(asNormal);
        }
    }
}
