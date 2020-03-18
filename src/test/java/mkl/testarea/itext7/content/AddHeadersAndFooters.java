package mkl.testarea.itext7.content;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.AreaBreakType;

/**
 * @author mkl
 */
public class AddHeadersAndFooters {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/60698558/how-to-get-page-size-of-pdf-document-itext-7">
     * How to get page size of pdf document iText 7
     * </a>
     * <p>
     * After reducing the OP's code in {@link #createPdf(String)} down to the
     * essentially required code to reproduce the issue, it became clear that
     * he ran into issues because iText had already flushed the pages he tried
     * to access. Thus, his issues can be resolved by instructing iText not to
     * flush pages early.
     * </p>
     */
    @Test
    public void testLikeDavids182009() throws IOException {
        createPdf(new File(RESULT_FOLDER, "davids182009.pdf").getAbsolutePath());
    }

    /** @see #testLikeDavids182009() */
    public void createPdf(String dest) throws IOException {

        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
//      To fix use this
        //try (Document document = new Document(pdf, pdf.getDefaultPageSize(), false)) {
//      instead of this
        try (Document document = new Document(pdf)) {

            document.setMargins(120, 36, 120, 36);

            document.add(new Paragraph("some content"));
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            document.add(new Paragraph("some more content"));
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            document.add(new Paragraph("still more content"));

            for (int i = 1; i <= document.getPdfDocument().getNumberOfPages(); i++) {

                System.out.println("PAGINA DEL PDF" + i);
                try {
                    Rectangle pageSize = document.getPdfDocument().getPage(i).getPageSize();
                    // Rectangle pageSize = document.getPdfDocument().getPage(i).getMediaBox();
                    System.out.println("RECTANGLE....." + pageSize);
                } catch (Exception e) {
                    // TODO: handle exception
                    System.out.println("EXCEPCION RECTANGULO..." + e);
                }
            }
        }
    }
}
