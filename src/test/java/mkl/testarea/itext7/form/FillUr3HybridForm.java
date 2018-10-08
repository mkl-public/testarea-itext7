package mkl.testarea.itext7.form;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

/**
 * @author mkl
 */
public class FillUr3HybridForm
{
    final static File RESULT_FOLDER = new File("target/test-outputs", "form");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="http://stackoverflow.com/questions/44097975/itext-7-net-makes-my-fields-readonly-automatically">
     * iText 7 .NET makes my fields readonly automatically
     * </a>
     * <br/>
     * <a href="https://www.uscis.gov/system/files_force/files/form/i-765.pdf?download=1">
     * i-765.pdf
     * </a>
     * <p>
     * The fields becoming "readonly" are due to the broken UR3 signature.
     * And without the UR3 signature the XFA form works only partially.
     * Removing the XFA form and the UR3 signature fixes the problem.
     * Unfortunately <code>new StampingProperties().preserveEncryption().useAppendMode()</code>
     * does not work.
     * </p>
     */
    @Test
    public void testFillI765LikeFmbishop() throws IOException
    {
        try (   InputStream resource = getClass().getResourceAsStream("i-765.pdf"))
        {
            //Initialize PDF reader and writer
            PdfReader reader = new PdfReader(resource);
            reader.setUnethicalReading(true);
            PdfWriter writer = new PdfWriter(new FileOutputStream(new File(RESULT_FOLDER, "i-765-filled.pdf")));

            //Initialize PDF document
            PdfDocument pdf = new PdfDocument(reader, writer/*, new StampingProperties().preserveEncryption().useAppendMode()*/);

            PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);
            Map<String, PdfFormField> fields = form.getFormFields();
            PdfFormField toSet = fields.get("form1[0].#subform[0].Line1_FamilyName[0]"); 
            toSet.setValue("Test familyname");

            form.removeXfaForm();
            pdf.getCatalog().remove(PdfName.Perms);
            //Close document
            pdf.close();
        }
    }

}
