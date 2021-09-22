package mkl.testarea.itext7.font;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfWriter;

/**
 * @author mkl
 */
public class FixForFirefox {
    final static File RESULT_FOLDER = new File("target/test-outputs", "font");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/69265360/convert-pdf-file-version-in-java-not-only-header-version">
     * Convert PDF file version in java (not only header version!)
     * </a>
     * <br/>
     * <a href="https://wetransfer.com/downloads/ce2d2f41ac29c36baa2ac895ebc0473c20210922065257/5889b2">
     * 1100-SD-9000455596.pdf
     * </a>
     * <p>
     * It turned out that the actual issue is that Firefox apparently cannot
     * properly determine the built-in encoding of the TrueType fonts, it
     * suffices to set the BaseEncoding of the font Encoding dictionaries to
     * something sensible, WinAnsiEncoding in this case.
     * </p>
     */
    @Test
    public void testFix1100_SD_9000455596() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("1100-SD-9000455596.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfWriter pdfWriter = new PdfWriter(new File(RESULT_FOLDER, "1100-SD-9000455596-Fixed.pdf"));
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter) ) {
            for (int page = 1; page <= pdfDocument.getNumberOfPages(); page++) {
                PdfPage pdfPage = pdfDocument.getPage(page);
                PdfResources pdfResources = pdfPage.getResources();
                for (Entry<PdfName, PdfObject> fontEntry : pdfResources.getResource(PdfName.Font).entrySet()) {
                    PdfObject fontObject = fontEntry.getValue();
                    if (fontObject != null && fontObject.getType() == PdfObject.INDIRECT_REFERENCE) {
                        fontObject = ((PdfIndirectReference)fontObject).getRefersTo(true);
                    }
                    if (fontObject instanceof PdfDictionary) {
                        PdfDictionary fontDictionary = (PdfDictionary) fontObject;
                        PdfDictionary encodingDictionary = fontDictionary.getAsDictionary(PdfName.Encoding);
                        if (encodingDictionary != null) {
                            if (encodingDictionary.getAsName(PdfName.BaseEncoding) == null &&
                                    encodingDictionary.getAsArray(PdfName.Differences) != null) {
                                encodingDictionary.put(PdfName.BaseEncoding, PdfName.WinAnsiEncoding);
                            }
                        }
                    }
                }
            }
        }
    }
}
