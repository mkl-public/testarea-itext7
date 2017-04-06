/**
 * 
 */
package mkl.testarea.itext7.cleanup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.licensekey.LicenseKey;
import com.itextpdf.pdfcleanup.PdfCleanUpLocation;
import com.itextpdf.pdfcleanup.PdfCleanUpTool;


/**
 * @author mklink
 *
 */
public class Redact
{
    final static File RESULT_FOLDER = new File("target/test-outputs", "cleanup");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="http://stackoverflow.com/questions/43211367/getting-exception-while-redacting-pdf-using-itext">
     * getting exception while redacting pdf using itext
     * </a>
     * <br/>
     * <a href="https://drive.google.com/file/d/0B-zalNTEeIOwM1JJVWctcW8ydU0/view?usp=drivesdk">
     * edited_120192824_5 (1).pdf
     * </a>
     * <p>
     * Indeed, the PdfClean classes throw a {@link NullPointerException} on page
     * 1 of this PDF. As it turns out, the cause is that the PDF makes use of a
     * construct which according to the PDF specification is obsolete and iText,
     * therefore, chose not to support. 
     * </p>
     */
    @Test
    public void testRedactLikeDevAvitesh() throws IOException
    {
        LicenseKey.loadLicenseFile("itextkey-multiple-products.xml");

        try (   InputStream resource = getClass().getResourceAsStream("edited_120192824_5 (1).pdf");
                PdfReader reader = new PdfReader(resource);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "edited_120192824_5 (1)-redacted.pdf"));
                PdfWriter writer = new PdfWriter(result);
                PdfDocument pdfDocument = new PdfDocument(reader, writer)   )
        {
            int pageCount = pdfDocument.getNumberOfPages();
            Rectangle linkLocation1 = new Rectangle(440f, 700f, 470f, 710f);
            Rectangle linkLocation2 = new Rectangle(308f, 205f, 338f, 215f);
            Rectangle linkLocation3 = new Rectangle(90f, 155f, 130f, 165f);
            List<PdfCleanUpLocation> cleanUpLocations = new ArrayList<PdfCleanUpLocation>();
            for (int currentPage = 1; currentPage <= pageCount; currentPage++) {
                if (currentPage == 1) {
                    cleanUpLocations.add(new PdfCleanUpLocation(currentPage,
                            linkLocation1, Color.BLACK));
                    cleanUpLocations.add(new PdfCleanUpLocation(currentPage,
                            linkLocation2, Color.BLACK));
                    cleanUpLocations.add(new PdfCleanUpLocation(currentPage,
                            linkLocation3, Color.BLACK));
                } else {
                    cleanUpLocations.add(new PdfCleanUpLocation(currentPage,
                            linkLocation1, Color.BLACK));
                }
            }

            PdfCleanUpTool cleaner = new PdfCleanUpTool(pdfDocument, cleanUpLocations);
            cleaner.cleanUp();
        }
    }

}
