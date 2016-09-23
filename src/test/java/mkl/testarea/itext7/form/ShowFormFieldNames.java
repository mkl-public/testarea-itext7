package mkl.testarea.itext7.form;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;

/**
 * @author mkl
 */
public class ShowFormFieldNames
{
    /**
     * <a href="http://stackoverflow.com/questions/39574021/how-can-the-internal-labels-of-the-editable-fields-in-an-acroform-pdf-be-found">
     * How can the internal labels of the editable fields in an acroform .pdf be found and listed?
     * </a>
     * <p>
     * This test method prints the fields of the arbitrarily chosen file "ds872.pdf". 
     * </p>
     */
    @Test
    public void testShowFieldsForDs872() throws IOException
    {
        String resourceName = "ds872.pdf";
        try (   InputStream resource = getClass().getResourceAsStream(resourceName);
                PdfDocument pdfDocument = new PdfDocument(new PdfReader(resource)))
        {
            List<String> fieldNames = getFormFieldNames(pdfDocument);

            System.out.printf("\nForm field names of '%s':\n", resourceName);
            for (String name : fieldNames)
            {
                System.out.printf("* '%s'\n", name);
            }
        }
    }

    /**
     * <a href="http://stackoverflow.com/questions/39574021/how-can-the-internal-labels-of-the-editable-fields-in-an-acroform-pdf-be-found">
     * How can the internal labels of the editable fields in an acroform .pdf be found and listed?
     * </a>
     * <p>
     * This method retrieves the form field names from the given {@link PdfDocument}. 
     * </p>
     */
    List<String> getFormFieldNames(PdfDocument pdfDocument)
    {
        PdfAcroForm pdfAcroForm = PdfAcroForm.getAcroForm(pdfDocument, false);
        if (pdfAcroForm == null)
            return Collections.emptyList();

        List<String> result = new ArrayList<>(pdfAcroForm.getFormFields().keySet());
        return result;
    }
}
