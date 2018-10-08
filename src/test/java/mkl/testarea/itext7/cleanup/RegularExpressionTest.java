package mkl.testarea.itext7.cleanup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.parser.listener.CharacterRenderInfo;
import com.itextpdf.licensekey.LicenseKey;
import com.itextpdf.pdfcleanup.autosweep.CompositeCleanupStrategy;
import com.itextpdf.pdfcleanup.autosweep.PdfAutoSweep;
import com.itextpdf.pdfcleanup.autosweep.RegexBasedCleanupStrategy;

/**
 * @author mklink
 */
public class RegularExpressionTest {
    final static File RESULT_FOLDER = new File("target/test-outputs", "cleanup");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/52635416/itext-pdfsweep-regexbasedcleanupstrategy-not-work-in-some-case">
     * iText PDFSweep RegexBasedCleanupStrategy not work in some case
     * </a>
     * <p>
     * Indeed, word boundaries at the end of lines might not be visible, cf.
     * the search for `"\\beinigen\\b"` here.
     * </p>
     * <p>
     * This can be fixed in the {@link CharacterRenderInfo} method
     * <code>mapString</code>: In the else-block of its
     * <code>if (chunk.sameLine(lastChunk))</code> structure first
     * execute a <code>sb.append(' ')</code> call.
     * </p>
     */
    @Test
    public void testWordBoundary() throws IOException {
        LicenseKey.loadLicenseFile("itextkey-multiple-products.xml");

        try (   InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf");
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "test-RegEx-cleaned.pdf"));
                PdfWriter writer = new PdfWriter(result)    ) {
            CompositeCleanupStrategy strategy = new CompositeCleanupStrategy();
            strategy.add(new RegexBasedCleanupStrategy("\\btest\\b").setRedactionColor(ColorConstants.PINK));
            strategy.add(new RegexBasedCleanupStrategy("\\beinigen\\b").setRedactionColor(ColorConstants.GRAY));

            // define a composite strategy
            
            writer.setCompressionLevel(0);
            
            PdfDocument pdf = new PdfDocument(new PdfReader(resource), writer);
            // sweep
            PdfAutoSweep pdfAutoSweep = new PdfAutoSweep(strategy);
            pdfAutoSweep.cleanUp(pdf);
            pdf.close();
        }
    }

}
