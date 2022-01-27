package mkl.testarea.itext7.content;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.PatternColor;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfPatternCanvas;
import com.itextpdf.kernel.pdf.colorspace.PdfPattern;
import com.itextpdf.kernel.pdf.colorspace.PdfPattern.Tiling;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;

/**
 * @author mkl
 */
public class AddWatermark {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/70554618/java-itext7-watermark-text-is-hiding-behind-images">
     * java itext7 watermark text is hiding behind images
     * </a>
     * <p>
     * This test executes the OP's code as is.
     * </p>
     */
    @Test
    public void testAddLikeFranco() throws IOException {
        try (   InputStream documentResource = getClass().getResourceAsStream("document.pdf");
                PdfReader documentReader = new PdfReader(documentResource);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "document-watermark.pdf"));
                PdfWriter pdfWriter = new PdfWriter(result);
                PdfDocument pdfDocument = new PdfDocument(documentReader, pdfWriter);   ) {
            PdfPattern.Tiling tiling = new Tiling(new Rectangle(220, 100));
            PdfFont helvetica = PdfFontFactory.createFont();
            new Canvas(new PdfPatternCanvas(tiling, pdfDocument), tiling.getBBox()).add(new Paragraph("uhatem")
                    .setFontColor(ColorConstants.GRAY)
                    .setFont(helvetica)
                    .setFontSize(15f)
                    .setRotationAngle(Math.PI / 10)
                );

            for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++) {
                PdfPage page = pdfDocument.getPage(i);
                new PdfCanvas(page.newContentStreamAfter(), page.getResources(), pdfDocument)
                        .saveState()
                        .setFillColor(new PatternColor(tiling))
                        .rectangle(page.getCropBox())
                        .fill()
                        .restoreState();
            }
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/70554618/java-itext7-watermark-text-is-hiding-behind-images">
     * java itext7 watermark text is hiding behind images
     * </a>
     * <p>
     * This test executes the OP's code with a twist: The watermark is added
     * using the blend mode Multiply.
     * </p>
     */
    @Test
    public void testAddLikeFrancoImproved() throws IOException {
        try (   InputStream documentResource = getClass().getResourceAsStream("document.pdf");
                PdfReader documentReader = new PdfReader(documentResource);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "document-watermark-improved.pdf"));
                PdfWriter pdfWriter = new PdfWriter(result);
                PdfDocument pdfDocument = new PdfDocument(documentReader, pdfWriter);   ) {
            PdfPattern.Tiling tiling = new Tiling(new Rectangle(220, 100));
            PdfFont helvetica = PdfFontFactory.createFont();
            new Canvas(new PdfPatternCanvas(tiling, pdfDocument), tiling.getBBox()).add(new Paragraph("uhatem")
                    .setFontColor(ColorConstants.GRAY)
                    .setFont(helvetica)
                    .setFontSize(15f)
                    .setRotationAngle(Math.PI / 10)
                );

            PdfExtGState extGState = new PdfExtGState().setBlendMode(new PdfName("Multiply"));
            for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++) {
                PdfPage page = pdfDocument.getPage(i);
                new PdfCanvas(page.newContentStreamAfter(), page.getResources(), pdfDocument)
                        .saveState()
                        .setExtGState(extGState)
                        .setFillColor(new PatternColor(tiling))
                        .rectangle(page.getCropBox())
                        .fill()
                        .restoreState();
            }
        }
    }
}
