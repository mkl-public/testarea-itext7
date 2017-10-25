package mkl.testarea.itext7.form;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfSignatureFormField;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;

/**
 * @author mkl
 */
public class AddSignatureField {
    final static File RESULT_FOLDER = new File("target/test-outputs", "form");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/46906725/adding-a-signature-form-field-to-a-page-its-visible-in-the-last-page-also">
     * Adding a signature form field to a page it's visible in the last page also
     * </a>
     * <p>
     * Indeed, the signature appears on the first and on the last page.
     * This is caused by the use of {@link PdfAcroForm#addField(PdfFormField)}
     * which is documented to add the field on the last page. The OP should use
     * {@link PdfAcroForm#addField(PdfFormField, com.itextpdf.kernel.pdf.PdfPage)}
     * instead.
     * </p>
     */
    @Test
    public void testAddLikeUser1498191() throws FileNotFoundException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new File(RESULT_FOLDER, "pippo.pdf"))))
        {
            //Add some blank pages
            pdfDoc.addNewPage();
            pdfDoc.addNewPage();
            pdfDoc.addNewPage();

            //Instantiate a Signature Form Field using factory
            PdfSignatureFormField sgnField = 
                PdfFormField.createSignature(pdfDoc, new Rectangle(100, 100, 200, 100));

            //setting name and page
            sgnField.setFieldName("pluto");
            sgnField.setPage(1);

            //Adding to AcroForm
            PdfAcroForm.getAcroForm(pdfDoc, true).addField(sgnField);
        }
    }
}
