package mkl.testarea.itext7.cleanup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.licensekey.LicenseKey;
import com.itextpdf.pdfcleanup.PdfCleanUpLocation;
import com.itextpdf.pdfcleanup.PdfCleanUpTool;

/**
 * @author mkl
 */
public class MoveSectionCleanly
{
    final static File RESULT_FOLDER = new File("target/test-outputs", "cleanup");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="http://stackoverflow.com/questions/40551553/moving-text-on-a-page-with-itext7-retaining-font-color-style-but-changing">
     * Moving text on a page with iText7 retaining font, color, style, … but changing size of the text
     * </a>
     * <p>
     * This example shows how to "move" content from a source rectangle to a target rectangle
     * without the text being still selectable in the source rectangle.
     * </p>
     */
    @Test
    public void testMoveCleanSectionBody() throws IOException
    {
        try (   InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/merge/Body.pdf"))
        {
            moveCleanSection(new PdfReader(resource),
                    new File(RESULT_FOLDER, "Body-moveClean.pdf").getAbsolutePath(),
                    1,
                    new Rectangle(100, 200, 100, 200),
                    new Rectangle(200, 300, 200, 300));
        }
    }

    /**
     * <a href="http://stackoverflow.com/questions/40551553/moving-text-on-a-page-with-itext7-retaining-font-color-style-but-changing">
     * Moving text on a page with iText7 retaining font, color, style, … but changing size of the text
     * </a>
     * <p>
     * This is the actual worker method which "moves" content from a source rectangle to a target
     * rectangle without the text being still selectable in the source rectangle.
     * </p>
     */
    public void moveCleanSection(PdfReader pdfReader, String targetFile, int page, Rectangle from, Rectangle to) throws IOException
    {
        LicenseKey.loadLicenseFile("itextkey-multiple-products.xml");

        ByteArrayOutputStream interimMain = new ByteArrayOutputStream();
        ByteArrayOutputStream interimPage = new ByteArrayOutputStream();
        ByteArrayOutputStream interimSection = new ByteArrayOutputStream();

        try (   PdfDocument pdfMainDocument = new PdfDocument(pdfReader);
                PdfDocument pdfPageDocument = new PdfDocument(new PdfWriter(interimPage)) )
        {
            pdfMainDocument.setCloseReader(false);
            pdfMainDocument.copyPagesTo(page, page, pdfPageDocument);
        }

        try (   PdfDocument pdfMainDocument = new PdfDocument(pdfReader, new PdfWriter(interimMain));
                PdfDocument pdfSectionDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(interimPage.toByteArray())), 
                new PdfWriter(interimSection))  )
        {

            List<PdfCleanUpLocation> cleanUpLocations = new ArrayList<PdfCleanUpLocation>();
            cleanUpLocations.add(new PdfCleanUpLocation(page, from, null));
            cleanUpLocations.add(new PdfCleanUpLocation(page, to, null));

            PdfCleanUpTool cleaner = new PdfCleanUpTool(pdfMainDocument, cleanUpLocations);
            cleaner.cleanUp();

            cleanUpLocations = new ArrayList<PdfCleanUpLocation>();
            Rectangle mediaBox = pdfSectionDocument.getPage(1).getMediaBox();

            if (from.getTop() < mediaBox.getTop())
                cleanUpLocations.add(new PdfCleanUpLocation(1, new Rectangle(mediaBox.getLeft(), from.getTop(), mediaBox.getWidth(), mediaBox.getTop() - from.getTop()), null));
            if (from.getBottom() > mediaBox.getBottom())
                cleanUpLocations.add(new PdfCleanUpLocation(1, new Rectangle(mediaBox.getLeft(), mediaBox.getBottom(), mediaBox.getWidth(), from.getBottom() -  mediaBox.getBottom()), null));
            if (from.getLeft() > mediaBox.getLeft())
                cleanUpLocations.add(new PdfCleanUpLocation(1, new Rectangle(mediaBox.getLeft(), mediaBox.getBottom(), from.getLeft() - mediaBox.getLeft(), mediaBox.getHeight()), null));
            if (from.getRight() < mediaBox.getRight())
                cleanUpLocations.add(new PdfCleanUpLocation(1, new Rectangle(from.getRight(), mediaBox.getBottom(), mediaBox.getRight() - from.getRight(), mediaBox.getHeight()), null));

            cleaner = new PdfCleanUpTool(pdfSectionDocument, cleanUpLocations);
            cleaner.cleanUp();
        }

        try (   PdfDocument pdfSectionDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(interimSection.toByteArray())));
                PdfDocument pdfMainDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(interimMain.toByteArray())), new PdfWriter(targetFile)) )
        {
            float scale = Math.min(to.getHeight() / from.getHeight(), to.getWidth() / from.getWidth());
            pdfSectionDocument.getPage(1).setMediaBox(from);
            PdfFormXObject pageXObject = pdfSectionDocument.getFirstPage().copyAsFormXObject(pdfMainDocument);
            PdfPage pdfPage = pdfMainDocument.getPage(page);
            PdfCanvas pdfCanvas = new PdfCanvas(pdfPage);
            pdfCanvas.addXObject(pageXObject, scale, 0, 0, scale, (to.getLeft() - from.getLeft() * scale), (to.getBottom() - from.getBottom() * scale));
        }
    }
}
