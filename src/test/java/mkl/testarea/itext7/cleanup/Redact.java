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

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.licensekey.LicenseKey;
import com.itextpdf.pdfcleanup.PdfCleanUpLocation;
import com.itextpdf.pdfcleanup.PdfCleanUpProcessor;
import com.itextpdf.pdfcleanup.PdfCleanUpTool;

/**
 * @author mkl
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
                            linkLocation1, ColorConstants.BLACK));
                    cleanUpLocations.add(new PdfCleanUpLocation(currentPage,
                            linkLocation2, ColorConstants.BLACK));
                    cleanUpLocations.add(new PdfCleanUpLocation(currentPage,
                            linkLocation3, ColorConstants.BLACK));
                } else {
                    cleanUpLocations.add(new PdfCleanUpLocation(currentPage,
                            linkLocation1, ColorConstants.BLACK));
                }
            }

            PdfCleanUpTool cleaner = new PdfCleanUpTool(pdfDocument, cleanUpLocations);
            cleaner.cleanUp();
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/44304695/itext-5-5-11-bold-text-looks-blurry-after-using-pdfcleanupprocessor">
     * iText 5.5.11 - bold text looks blurry after using PdfCleanUpProcessor
     * </a>
     * <br/>
     * <a href="http://s000.tinyupload.com/index.php?file_id=52420782334200922303">
     * before.pdf
     * </a>
     * <p>
     * By comparing the before and after files it becomes clear that for some reason
     * the {@link PdfCleanUpProcessor} falsely drops general graphics state operations
     * (at least w, J, and d).
     * </p>
     * <p>
     * In the before document in particular the w operation is important for the text
     * because a poor man's bold variant is used, i.e. instead of using an actual bold
     * font the normal font is used and the text rendering mode is set to not only fill
     * the glyph contours but also draw a line along it giving it a bold'ish appearance.
     * </p>
     * <p>
     * The width of that line is set to 0.23333 using a w operation. As that operation
     * is missing in the after document, the default width value of 1 is used. Thus,
     * the line along the contour now is 4 times as big as before resulting in a very
     * fat appearance.
     * </p>
     * <p>
     * Unfortunately this test actually shows a different error: the observations above
     * have been made by comparison with an additional "after.pdf" provided by the OP,
     * not with the output of this test. The cause: before.pdf and after.pdf had been
     * redacted by a different tool before being shared, and the way this tools redacts
     * makes iText cleanup fail: It leaves TJ operations with only numbers inside which
     * makes iText throw an exception.
     * </p>
     */
    @Test
    public void testRedactLikeTieco() throws IOException
    {
        LicenseKey.loadLicenseFile("itextkey-multiple-products.xml");

        try (   InputStream resource = getClass().getResourceAsStream("before.pdf");
                PdfReader reader = new PdfReader(resource);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "before-redacted.pdf"));
                PdfWriter writer = new PdfWriter(result);
                PdfDocument pdfDocument = new PdfDocument(reader, writer)   )
        {
            List<PdfCleanUpLocation> cleanUpLocations = new ArrayList<PdfCleanUpLocation>();
            cleanUpLocations.add(new PdfCleanUpLocation(1, new Rectangle(0f, 0f, 595f, 680f)));

            PdfCleanUpTool cleaner = new PdfCleanUpTool(pdfDocument, cleanUpLocations);
            cleaner.cleanUp();
        }
    }
}
