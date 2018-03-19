package mkl.testarea.itext7.extract;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.utils.TaggedPdfReaderTool;

/**
 * @author mkl
 */
public class ExtractTaggedText {
    final static File RESULT_FOLDER = new File("target/test-outputs", "extract");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/49372388/which-is-the-best-way-to-read-data-from-a-table-in-a-pdf">
     * which is the best way to read data from a table in a pdf?
     * </a>
     * <br/>
     * <a href="https://www.fnbaloncesto.com/img/noticias/103/pdfs/ENaB%2020180317.pdf">
     * ENaB 20180317.pdf
     * </a>
     * <p>
     * This test shows how to extract structured information from a tagged PDF.
     * </p>
     */
    @Test
    public void testENaB20180317() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("ENaB 20180317.pdf");
                PdfReader reader = new PdfReader(resource);
                PdfDocument document = new PdfDocument(reader)) {
            TaggedPdfReaderTool tool = new TaggedPdfReaderTool(document);
            try (   OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "ENaB 20180317.txt"))) {
                tool.convertToXml(result);
            }
        }
    }

}
