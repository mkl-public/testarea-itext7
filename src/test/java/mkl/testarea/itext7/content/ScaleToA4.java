package mkl.testarea.itext7.content;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;

import mkl.testarea.itext7.merge.MarginFinder;

/**
 * @author mkl
 */
public class ScaleToA4 {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/63505060/java-itext-scale-document-to-a4">
     * Java iText scale document to A4
     * </a>
     * <p>
     * This test applies {@link #scale(PdfDocument, Rectangle, Rectangle)} to
     * scale to A4 with 1" margins.
     * </p>
     */
    @Test
    public void testFdaRequiresUseOfEctdFormatAndStandardizedStudyDataInFutureRegulatorySubmissionsSept() throws IOException {
        Rectangle pageSize = PageSize.A4;
        Rectangle pageBodySize = pageSize.clone().applyMargins(72, 72, 72, 72, false);
        
        try (   InputStream resource = getClass().getResourceAsStream("_FDA_Requires_Use_of_eCTD_Format_and_Standardized_Study_Data_in_Future_Regulatory_Submissions__Sept.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfWriter pdfWriter = new PdfWriter(new File(RESULT_FOLDER, "_FDA_Requires_Use_of_eCTD_Format_and_Standardized_Study_Data_in_Future_Regulatory_Submissions__Sept-A4.pdf"));
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter)) {
            scale(pdfDocument, pageSize, pageBodySize);
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/63505060/java-itext-scale-document-to-a4">
     * Java iText scale document to A4
     * </a>
     * <p>
     * This test applies {@link #scale(PdfDocument, Rectangle, Rectangle)} to
     * scale to A4 with 1" margins.
     * </p>
     */
    @Test
    public void test021549Orig1s025AprepitantClinpharmPreaMac() throws IOException {
        Rectangle pageSize = PageSize.A4;
        Rectangle pageBodySize = pageSize.clone().applyMargins(72, 72, 72, 72, false);
        
        try (   InputStream resource = getClass().getResourceAsStream("021549Orig1s025_aprepitant_clinpharm_prea_Mac.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfWriter pdfWriter = new PdfWriter(new File(RESULT_FOLDER, "021549Orig1s025_aprepitant_clinpharm_prea_Mac-A4.pdf"));
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter)) {
            scale(pdfDocument, pageSize, pageBodySize);
        }
    }

    void scale(PdfDocument pdfDocument, Rectangle pageSize, Rectangle pageBodySize) {
        int n = pdfDocument.getNumberOfPages();

        for (int i = 1; i <= n; i++) {
            PdfPage page = pdfDocument.getPage(i);

            MarginFinder marginFinder = new MarginFinder();
            PdfCanvasProcessor pdfCanvasProcessor = new PdfCanvasProcessor(marginFinder);
            pdfCanvasProcessor.processPageContent(page);
            Rectangle boundingBox = marginFinder.getBoundingBox();
            if (boundingBox == null || boundingBox.getWidth() == 0 || boundingBox.getHeight() == 0) {
                System.err.printf("Cannot scale page %d contents with bounding box %s\n", i , boundingBox);
                continue;
            } else {
                // Scale and move content into A4 with margin
                double scale = 0, xDiff= 0, yDiff = 0;
                double xScale = pageBodySize.getWidth()/boundingBox.getWidth();
                double yScale = pageBodySize.getHeight()/boundingBox.getHeight();
                if (xScale < yScale) {
                    yDiff = boundingBox.getHeight() * (yScale / xScale - 1) / 2;
                    scale = xScale;
                } else {
                    xDiff = boundingBox.getWidth() * (xScale / yScale - 1) / 2;
                    scale = yScale;
                }

                AffineTransform transform = AffineTransform.getTranslateInstance(pageBodySize.getLeft() + xDiff, pageBodySize.getBottom() + yDiff);
                transform.scale(scale, scale);
                transform.translate(-boundingBox.getLeft(), -boundingBox.getBottom());
                new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDocument)
                        .concatMatrix(transform);
            }
            page.setMediaBox(pageSize);
            page.setCropBox(pageSize);
        }
    }
}
