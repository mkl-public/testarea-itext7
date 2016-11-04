package mkl.testarea.itext7.form;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfSignatureFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.layout.Document;

/**
 * @author mkl
 */
public class AddFieldWithValue
{
    final static File RESULT_FOLDER = new File("target/test-outputs", "form");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="http://stackoverflow.com/questions/40406256/itext7-cannot-set-formfield-value-without-getting-error">
     * iText7, cannot set formField value without getting error
     * </a>
     * <p>
     * Indeed, the OP's original code runs into an issue. But adding the form field
     * before trying to set its value (cf. {@link #addAcroForm(PdfDocument, Document)})
     * fixes this.
     * </p>
     */
    @Test
    public void testAddLineElliot() throws IOException
    {
        try (   InputStream resource = getClass().getResourceAsStream("test-orig.pdf"))
        {
            //Initialize PDF reader and writer
            PdfReader reader = new PdfReader(resource);
            PdfWriter writer = new PdfWriter(new FileOutputStream(new File(RESULT_FOLDER, "test-orig-signed.pdf")));

            //Initialize PDF document
            PdfDocument pdf = new PdfDocument(reader, writer);

            // Initialize document
            Document document = new Document(pdf);

            addAcroForm(pdf, document);

            //Close document
            document.close();
        }
    }

    /**
     * <a href="http://stackoverflow.com/questions/40406256/itext7-cannot-set-formfield-value-without-getting-error">
     * iText7, cannot set formField value without getting error
     * </a>
     * <p>
     * Indeed, the OP's original code runs into an issue. But adding the form field
     * before trying to set its value (cf. the line marked "Addition") fixes this.
     * </p>
     */
    public static PdfAcroForm addAcroForm(PdfDocument pdf, Document doc) throws IOException {
        int numPages = pdf.getNumberOfPages();
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);

        PdfTextFormField confField = PdfFormField.createText(pdf);
        confField.setFieldName("confirmation");

        PdfSignatureFormField sigField = PdfFormField.createSignature(pdf);
        sigField.setFieldName("signature");

        PdfWidgetAnnotation firstPageConf = new PdfWidgetAnnotation(new Rectangle(0, 0, 425, 15));
        PdfWidgetAnnotation pageConf = new PdfWidgetAnnotation(new Rectangle(0, 0, 425, 15));
        PdfWidgetAnnotation signature = new PdfWidgetAnnotation(new Rectangle(0, 100, 425, 15));

        //add conf annotation based on first page or not
        for (int i = 1; i <= numPages; i++) {
            PdfPage page = pdf.getPage(i);

            if (i == 1) {
                page.addAnnotation(firstPageConf);
            } else {
                page.addAnnotation(pageConf);
            }
        }

        form.addField(confField);
        form.addField(sigField);        

        confField.addKid(firstPageConf);
        confField.addKid(pageConf);

        sigField.addKid(signature);   

        //this one is different because we try to set a value....
        PdfTextFormField testField = PdfFormField.createText(pdf);
        testField.setFieldName("test");
        PdfWidgetAnnotation test = new PdfWidgetAnnotation(new Rectangle(0, 100, 425, 15));
        testField.addKid(test);
        pdf.getPage(1).addAnnotation(test);
        // vvv--- Addition
        form.addField(testField);
        // ^^^--- Addition
        testField.setValue("testValue");//error 'object.must.be.indirect.to.work.with.this.wrapper' occurs here

        return form;

    }
}
