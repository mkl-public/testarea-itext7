package mkl.testarea.itext7.content;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;

/**
 * @author mkl
 */
public class AddPagenumberToCopy {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/52743566/adding-page-number-text-to-pdf-copy-gets-flipped-mirrored-with-itext-7">
     * Adding page number text to pdf copy gets flipped/mirrored with itext 7
     * </a>
     * <br/>
     * <a href="https://drive.google.com/open?id=11_9ptuoRqS91hI3fDcs2FRsIUEiX0a84">
     * li.persia.pdf
     * </a>
     * <p>
     * Indeed, the generated page numbers appear upside down somewhere in the
     * middle of the page.
     * </p>
     * @see #testLikeAibanezFixed()
     */
    @Test
    public void testLikeAibanez() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("li.persia.pdf");
                PdfDocument pdfDocSrc = new PdfDocument(new PdfReader(resource));
                FileOutputStream fos = new FileOutputStream(new File(RESULT_FOLDER, "li.persia-copied-with-page-numbers.pdf"));
                PdfDocument pdfDocTgt = new PdfDocument(new PdfWriter(fos));
                Document document = new Document(pdfDocTgt);    ) {
            copyPdfPages(pdfDocSrc, document, 1, pdfDocSrc.getNumberOfPages(), 1);
        }
    }

    /**
     * @see #testLikeAibanez()
     */
    private static int copyPdfPages(PdfDocument source, Document document, Integer start, Integer pages, Integer number) {
        int oldC;
        int max = start + pages - 1;
        Text text;
        for (oldC = start; oldC <= max; oldC++) {
            text = new Text(String.format("Page %d", number));
            PageSize pageSize = source.getDefaultPageSize();
            source.copyPagesTo(oldC, oldC, document.getPdfDocument());
            document.add(new Paragraph(text).setBorder(new SolidBorder(ColorConstants.RED, 1))
                    .setFixedPosition(number++, pageSize.getWidth() - 55, pageSize.getHeight() - 30, 50));
        }
        return oldC - start;
    }

    /**
     * <a href="https://stackoverflow.com/questions/52743566/adding-page-number-text-to-pdf-copy-gets-flipped-mirrored-with-itext-7">
     * Adding page number text to pdf copy gets flipped/mirrored with itext 7
     * </a>
     * <br/>
     * <a href="https://drive.google.com/open?id=11_9ptuoRqS91hI3fDcs2FRsIUEiX0a84">
     * li.persia.pdf
     * </a>
     * <p>
     * Here the generated page numbers appear in the upper right corner
     * of the pages without being fliped or rotated. The fix is to use a
     * {@link PdfCanvas} constructed with the <code>wrapOldContent</code>
     * parameter set to <code>true</code>. 
     * </p>
     * @see #testLikeAibanez()
     */
    @Test
    public void testLikeAibanezFixed() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("li.persia.pdf");
                PdfDocument pdfDocSrc = new PdfDocument(new PdfReader(resource));
                FileOutputStream fos = new FileOutputStream(new File(RESULT_FOLDER, "li.persia-copied-with-page-numbers-fixed.pdf"));
                PdfDocument pdfDocTgt = new PdfDocument(new PdfWriter(fos))    ) {
            copyPdfPagesFixed(pdfDocSrc, pdfDocTgt, 1, pdfDocSrc.getNumberOfPages(), 1);
        }
    }

    /**
     * @see #testLikeAibanezFixed()
     */
    private static int copyPdfPagesFixed(PdfDocument source, PdfDocument target, int start, int pages, int number) {
        int oldC;
        int max = start + pages - 1;
        Text text;
        for (oldC = start; oldC <= max; oldC++) {
            text = new Text(String.format("Page %d", number));
            source.copyPagesTo(oldC, oldC, target);
            PdfPage newPage = target.getLastPage();
            Rectangle pageSize = newPage.getCropBox();
            try (   Canvas canvas = new Canvas(new PdfCanvas(newPage, true), target, pageSize)  ) {
                canvas.add(new Paragraph(text).setBorder(new SolidBorder(ColorConstants.RED, 1))
                      .setFixedPosition(number++, pageSize.getWidth() - 55, pageSize.getHeight() - 30, 50));
            }
        }
        return oldC - start;
    }
}
