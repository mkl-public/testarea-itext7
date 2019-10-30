package mkl.testarea.itext7.content;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;

/**
 * @author mkl
 */
public class RemoveContent {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/58329971/filter-out-anything-but-interactive-form-fields-in-pdfs">
     * Filter out anything but interactive form fields in PDF's
     * </a>
     * <p>
     * This test shows how to remove the content streams of the document pages
     * to filter out anything static.
     * </p>
     */
    @Test
    public void testRemoveAllPageContentStreams() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/form/1540982441_313554925_acro_sample_empty_fields_signedFinal.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfWriter pdfWriter = new PdfWriter(new File(RESULT_FOLDER, "1540982441_313554925_acro_sample_empty_fields_signedFinal-noContent.pdf"));
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter, new StampingProperties().useAppendMode())   ) {
            for (int pageNr = 1; pageNr <= pdfDocument.getNumberOfPages(); pageNr++) {
                PdfPage pdfPage = pdfDocument.getPage(pageNr);
                pdfPage.getPdfObject().remove(PdfName.Contents);
                pdfPage.getPdfObject().setModified();
            }
        }
    }

}
