package mkl.testarea.itext7.content;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

/**
 * @author mkl
 */
public class RotateText {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/70786737/how-to-rotate-a-specific-text-in-a-pdf-which-is-already-present-in-the-pdf-using">
     * How to rotate a specific text in a pdf which is already present in the pdf using itext or pdfbox?
     * </a>
     * <br/>
     * <a href="https://drive.google.com/file/d/1AUhsaWz3qwj5zbOgQ9r3cOAXDsHSLB1s/view?usp=sharing">
     * Before.pdf
     * </a> as "BeforeAkhilNagaSai.pdf"
     * <p>
     * This test illustrates how to rotate text in an existing PDF using the
     * {@link LimitedTextRotater} applied to the OP's example file.
     * </p>
     */
    @Test
    public void testBeforeAkhilNagaSai() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("BeforeAkhilNagaSai.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "BeforeAkhilNagaSai-rotated.pdf"));
                PdfWriter pdfWriter = new PdfWriter(result);
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter) )
        {
            PdfCanvasEditor editor = new LimitedTextRotater(new Matrix(0, -1, 1, 0, 0, 0), text -> true);
            for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++){
                editor.editPage(pdfDocument, i);
            }
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/70786737/how-to-rotate-a-specific-text-in-a-pdf-which-is-already-present-in-the-pdf-using">
     * How to rotate a specific text in a pdf which is already present in the pdf using itext or pdfbox?
     * </a>
     * <p>
     * This test illustrates how to rotate text in an existing PDF using the
     * {@link LimitedTextRotater} applied to another file. Here actually not
     * merely a rotation but also a scaling is applied.
     * </p>
     */
    @Test
    public void testDocument() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("document.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "document-rotated.pdf"));
                PdfWriter pdfWriter = new PdfWriter(result);
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter) )
        {
            PdfCanvasEditor editor = new LimitedTextRotater(new Matrix(1, -1, 1, 1, 0, 0), text -> text.contains("Pflicht"));
            for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++){
                editor.editPage(pdfDocument, i);
            }
        }
    }
}
