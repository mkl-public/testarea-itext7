package mkl.testarea.itext7.content;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

/**
 * @author mkl
 */
public class AddContentToFirstPage {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/48397057/itext7-adding-content-from-existing-pdf-to-a-new-one">
     * itext7 - adding content from existing PDF to a new one
     * </a>
     * <p>
     * This sample shows how to stamp a template onto the existing first page
     * of the target document instead of onto a newly inserted blank first page.
     * </p>
     */
    @Test
    public void test() throws IOException {
        try (   InputStream documentResource = getClass().getResourceAsStream("document.pdf");
                PdfReader documentReader = new PdfReader(documentResource);
                InputStream templateResource = getClass().getResourceAsStream("test.pdf");
                PdfReader templateReader = new PdfReader(templateResource);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "document-with-test.pdf"));
                PdfWriter pdfWriter = new PdfWriter(result);
                PdfDocument pdfDocument = new PdfDocument(documentReader, pdfWriter);
                PdfDocument pdfTemplate = new PdfDocument(templateReader))
        {
            PdfPage origPage = pdfTemplate.getPage(1);
            Rectangle orig = origPage.getPageSize();

            //PdfPage page = pdfDocument.addNewPage(1, PageSize.A4);
            PdfPage page = pdfDocument.getPage(1);

            PdfCanvas canvas = new PdfCanvas(page);
            AffineTransform transformationMatrix = AffineTransform.getScaleInstance(
                      page.getPageSize().getWidth() / orig.getWidth(),
                      page.getPageSize().getHeight() / orig.getHeight());

            canvas.concatMatrix(transformationMatrix);
            PdfFormXObject pageCopy = origPage.copyAsFormXObject(pdfDocument); 

            canvas.addXObject(pageCopy, 0, 0);
        }
    }

}
