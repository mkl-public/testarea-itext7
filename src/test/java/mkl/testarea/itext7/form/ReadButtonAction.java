package mkl.testarea.itext7.form;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;

/**
 * @author mkl
 */
public class ReadButtonAction {
    /**
     * <a href="https://stackoverflow.com/questions/72074065/how-to-detect-which-signature-field-is-linked-to-the-corresponding-btn-at-run-ti">
     * How to detect which signature field is linked to the corresponding btn at run time?
     * </a>
     * <br/>
     * <a href="https://drive.google.com/file/d/1gws1sO6qIIOqlCe1BXqQcCR3okQWFQ0c/view?usp=sharing">
     * form_field_document.pdf
     * </a>
     * <p>
     * This test shows how to retrieve the JavaScript from a PDF button JavaScript action.
     * </p>
     */
    @Test
    public void testReadSignButtonAction() throws IOException {
        try (
            InputStream resource = getClass().getResourceAsStream("form_field_document.pdf");
            PdfReader pdfReader = new PdfReader(resource);
            PdfDocument pdfDocument = new PdfDocument(pdfReader);
        ) {
            PdfAcroForm pdfAcroForm = PdfAcroForm.getAcroForm(pdfDocument, false);
            PdfFormField btnSignField = pdfAcroForm.getField("btn_sign");
            PdfDictionary actionDictionary = btnSignField.getWidgets().get(0).getAction();
            if (PdfName.JavaScript.equals(actionDictionary.getAsName(PdfName.S))) {
                PdfObject jsObject = actionDictionary.get(PdfName.JS, true);
                if (jsObject instanceof PdfString) {
                    System.out.print(((PdfString)jsObject).getValue());
                } else if (jsObject instanceof PdfStream) {
                    PdfStream jsStream = (PdfStream)jsObject;
                    System.out.print(new String(jsStream.getBytes()));
                }
            }
        }

    }

}
