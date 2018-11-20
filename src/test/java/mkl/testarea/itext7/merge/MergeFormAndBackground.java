package mkl.testarea.itext7.merge;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

/**
 * @author mkl
 */
public class MergeFormAndBackground {
    final static File RESULT_FOLDER = new File("target/test-outputs", "merge");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/53385456/how-to-embed-all-fonts-from-other-pdf-itext-7">
     * How to embed all fonts from other PDF iText 7
     * </a>
     * <p>
     * This test shows that it's much easier to copy the background under
     * the form instead of copying the form over the background.
     * </p>
     */
    @Test
    public void testCopyBackgroundUnderForm() throws IOException {
        try (   InputStream formStream = getClass().getResourceAsStream("/mkl/testarea/itext7/form/acro_sample_empty_fields.pdf");
                InputStream backStream = getClass().getResourceAsStream("/mkl/testarea/itext7/content/document.pdf");
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "acro_sample_empty_fields-plus-document-under.pdf"))   )
        {
            PdfReader formReader = new PdfReader(formStream);
            PdfReader backReader = new PdfReader(backStream);
            PdfWriter writer = new PdfWriter(result);
            
            try (   PdfDocument source = new PdfDocument(backReader);
                    PdfDocument target = new PdfDocument(formReader, writer)    )
            {
                PdfFormXObject xobject = source.getPage(1).copyAsFormXObject(target);
                PdfPage targetFirstPage = target.getFirstPage();
                PdfStream stream = targetFirstPage.newContentStreamBefore();
                PdfCanvas pdfCanvas = new PdfCanvas(stream, targetFirstPage.getResources(), target);
                Rectangle cropBox = targetFirstPage.getCropBox();
                pdfCanvas.addXObject(xobject, cropBox.getX(), cropBox.getY());
            }
        }
    }

}
