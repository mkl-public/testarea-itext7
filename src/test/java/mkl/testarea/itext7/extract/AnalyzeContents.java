package mkl.testarea.itext7.extract;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.google.common.collect.Multimap;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;

/**
 * <p>
 * This test uses the {@link ContentAnalyzer} to check whether there
 * are images covering text in the example PDFs from
 * https://www.pdf-insecurity.org/signature-shadow/shadow-attacks.html
 * provided as examples for Shadow Attacks. In the hide example such
 * images are observed.
 * </p>
 * @author mkl
 */
public class AnalyzeContents {
    @Test
    public void testHideShadowFile() throws IOException {
        System.out.println("\n\nhide-shadow-file.pdf\n=====\n");
        try (InputStream resource = getClass().getResourceAsStream("hide-shadow-file.pdf")) {
            analyzePdf(resource);
        }
    }

    public void analyzePdf(InputStream pdfStream) throws IOException {
        try (ContentAnalyzer analyzer = new ContentAnalyzer(new RandomAccessSourceFactory().createSource(pdfStream), new ReaderProperties())) {
            Multimap<Integer, HiddenText> hiddenTexts = analyzer.findHiddenTexts();
            for (Integer pageNr : hiddenTexts.keySet()) {
                System.out.printf(" * Page %d:\n", pageNr);
                for (HiddenText hiddenText : hiddenTexts.get(pageNr)) {
                    PdfXObject xobject = hiddenText.getXobject();
                    PdfIndirectReference reference = xobject.getPdfObject().getIndirectReference();
                    System.out.printf("   - \"%s\" hidden by XObject %s\n", hiddenText.getText(), reference != null ? String.valueOf(reference.getObjNumber()) : "-");
                }
                
            }
            if (hiddenTexts.isEmpty()) {
                System.out.println("  No hidden texts found");
            }
        }
    }
}
