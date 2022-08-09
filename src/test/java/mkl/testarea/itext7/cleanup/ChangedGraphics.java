package mkl.testarea.itext7.cleanup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.pdfcleanup.CleanUpProperties;
import com.itextpdf.pdfcleanup.PdfCleanUpLocation;
import com.itextpdf.pdfcleanup.PdfCleanUpTool;
import com.itextpdf.pdfcleanup.autosweep.ICleanupStrategy;
import com.itextpdf.pdfcleanup.autosweep.PdfAutoSweepTools;
import com.itextpdf.pdfcleanup.autosweep.RegexBasedCleanupStrategy;

/**
 * @author mklink
 *
 */
public class ChangedGraphics {
    final static File RESULT_FOLDER = new File("target/test-outputs", "cleanup");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/64462607/using-pdfcleanuptool-or-pdfautosweep-causes-some-text-to-change-to-bold-line-we">
     * Using PdfCleanUpTool or PdfAutoSweep causes some text to change to bold, line weights increase and double points change to hearts
     * </a>
     * <br/>
     * <a href="https://drive.google.com/drive/folders/1hldcKYopupZivQzD7Wdbx7GN5P2eLDOo?usp=sharin">
     * TEST_PDF.pdf
     * </a>
     * <p>
     * Cannot reproduce the issue. But the OP appears to use older versions.
     * </p>
     */
    @Test
    public void testJukkaLaattala1() throws IOException {
        File dest = new File(RESULT_FOLDER, "orientation_result.pdf"); 
        try (   InputStream resource = getClass().getResourceAsStream("TEST_PDF.pdf")) {
            PdfDocument pdfDoc = new PdfDocument(new PdfReader(resource), new PdfWriter(dest));

            List<PdfCleanUpLocation> cleanUpLocations = new ArrayList<PdfCleanUpLocation>();

            // The arguments of the PdfCleanUpLocation constructor: the number of page to be cleaned up,
            // a Rectangle defining the area on the page we want to clean up,
            // a color which will be used while filling the cleaned area.
            PdfCleanUpLocation location = new PdfCleanUpLocation(1, new Rectangle(97, 405, 383, 40),
                    ColorConstants.GRAY);
            cleanUpLocations.add(location);

            PdfCleanUpTool cleaner = new PdfCleanUpTool(pdfDoc, cleanUpLocations, new CleanUpProperties());
            cleaner.cleanUp();

            pdfDoc.close();
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/64462607/using-pdfcleanuptool-or-pdfautosweep-causes-some-text-to-change-to-bold-line-we">
     * Using PdfCleanUpTool or PdfAutoSweep causes some text to change to bold, line weights increase and double points change to hearts
     * </a>
     * <br/>
     * <a href="https://drive.google.com/drive/folders/1hldcKYopupZivQzD7Wdbx7GN5P2eLDOo?usp=sharin">
     * TEST_PDF.pdf
     * </a>
     * <p>
     * Cannot reproduce the issue. But the OP appears to use older versions.
     * </p>
     */
    @Test
    public void testJukkaLaattala2() throws IOException {
        File dest = new File(RESULT_FOLDER, "orientation_result2.pdf"); 
        try (   InputStream resource = getClass().getResourceAsStream("TEST_PDF.pdf")) {
            PdfDocument pdfDoc = new PdfDocument(new PdfReader(resource), new PdfWriter(dest));

            // If the second argument is true, then regions to be erased are extracted from the redact annotations
            // contained inside the given document. If the second argument is false (that's default behavior),
            // then use PdfCleanUpTool.addCleanupLocation(PdfCleanUpLocation)
            // method to set regions to be erased from the document.
            PdfCleanUpTool cleaner = new PdfCleanUpTool(pdfDoc, true, new CleanUpProperties());
            cleaner.cleanUp();

            pdfDoc.close();
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/64462607/using-pdfcleanuptool-or-pdfautosweep-causes-some-text-to-change-to-bold-line-we">
     * Using PdfCleanUpTool or PdfAutoSweep causes some text to change to bold, line weights increase and double points change to hearts
     * </a>
     * <br/>
     * <a href="https://drive.google.com/drive/folders/1hldcKYopupZivQzD7Wdbx7GN5P2eLDOo?usp=sharin">
     * TEST_PDF.pdf
     * </a>
     * <p>
     * Cannot reproduce the issue. But the OP appears to use older versions.
     * </p>
     */
    @Test
    public void testJukkaLaattala3() throws IOException {
        File dest = new File(RESULT_FOLDER, "orientation_result3.pdf"); 
        try (   InputStream resource = getClass().getResourceAsStream("TEST_PDF.pdf")) {
            try (PdfDocument pdf = new PdfDocument(new PdfReader(resource), new PdfWriter(dest))) {
                final ICleanupStrategy cleanupStrategy = new RegexBasedCleanupStrategy(Pattern.compile("2019", Pattern.CASE_INSENSITIVE)).setRedactionColor(ColorConstants.PINK);
                final PdfAutoSweepTools autoSweep = new PdfAutoSweepTools(cleanupStrategy);
                autoSweep.tentativeCleanUp(pdf);
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/73279755/pdfcleanuptool-setredactioncolor">
     * PdfCleanUpTool SetRedactionColor
     * </a>
     * <p>
     * In a comment the OP mentions that with the clean up color set to <code>null</code>
     * the clean up process crashes. This cannot be reproduced.
     * </p>
     */
    @Test
    public void testJukkaLaattala1NoColor() throws IOException {
        File dest = new File(RESULT_FOLDER, "orientation_result_no-color.pdf"); 
        try (   InputStream resource = getClass().getResourceAsStream("TEST_PDF.pdf")) {
            PdfDocument pdfDoc = new PdfDocument(new PdfReader(resource), new PdfWriter(dest));

            List<PdfCleanUpLocation> cleanUpLocations = new ArrayList<PdfCleanUpLocation>();

            // The arguments of the PdfCleanUpLocation constructor: the number of page to be cleaned up,
            // a Rectangle defining the area on the page we want to clean up,
            // a color which will be used while filling the cleaned area.
            PdfCleanUpLocation location = new PdfCleanUpLocation(1, new Rectangle(97, 405, 383, 40), null);
            cleanUpLocations.add(location);

            PdfCleanUpTool cleaner = new PdfCleanUpTool(pdfDoc, cleanUpLocations, new CleanUpProperties());
            cleaner.cleanUp();

            pdfDoc.close();
        }
    }
}
