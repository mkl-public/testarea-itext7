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
import com.itextpdf.kernel.pdf.StampingProperties;

/**
 * @author mkl
 */
public class FillFieldInAppendMode {
    final static File RESULT_FOLDER = new File("target/test-outputs", "form");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/57785280/itextsharp-acroform-field-value-disappears-after-singing-pdf-document">
     * itextsharp - AcroForm field value disappears after singing PDF-Document
     * </a>
     * <br/>
     * <a href="https://drive.google.com/file/d/105iPaoWYKN8Mk4srds_ntMkGLo4XMrrm/view?usp=sharing">
     * example.pdf
     * </a>, of which the revision after form-fill-in has been extracted as "example.002.pdf".
     * <p>
     * In contrast to iText 5.x and before, iText 7 apparently does
     * not forget to mark its changes for addition to the incremental
     * update.
     * </p>
     */
    @Test
    public void testFillLikeCeroun() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("example.002.pdf");
                PdfReader reader = new PdfReader(resource);
                PdfWriter writer = new PdfWriter(new File(RESULT_FOLDER, "example.002-filled.pdf"));
                PdfDocument document = new PdfDocument(reader, writer, new StampingProperties().useAppendMode())) {
            PdfAcroForm form = PdfAcroForm.getAcroForm(document, true);
            form.getField("AcroFormField_0").setValue("test1234");
        }
    }

}
