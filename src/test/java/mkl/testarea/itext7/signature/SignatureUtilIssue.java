package mkl.testarea.itext7.signature;

import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.SignatureUtil;

/**
 * @author mkl
 */
public class SignatureUtilIssue {

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * <a href="https://stackoverflow.com/questions/62518805/itext-7-signatureutil-signed-docs-gets-no-associate-pdfwriter-for-making-indi">
     * IText 7 SignatureUtil - signed docs gets “no associate PdfWriter for making indirects”
     * </a>
     * <br/>
     * <a href="https://drive.google.com/file/d/1biuG9pIOS2piIBLNFNFrV2bhH_U9nk6E/view?usp=sharing">
     * itext7_signatureutil_issue_doc.pdf
     * </a>
     * <p>
     * Indeed, an error occurs. The reason is an error in the PDF: Its
     * Info dictionary is referenced as a field dictionary from the
     * AcroForm Fields array. iText attempts to "repair" this field
     * which fails in a read-only document.
     * </p>
     */
    @Test
    public void testConstructSignatureUtil() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("itext7_signatureutil_issue_doc.pdf");
                PdfReader signedPdfReader = new PdfReader(resource);
                PdfDocument signedPdf = new PdfDocument(signedPdfReader)    ) {
            SignatureUtil signatureUtil = new SignatureUtil(signedPdf);
        }
    }

}
