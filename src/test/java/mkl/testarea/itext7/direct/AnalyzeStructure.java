package mkl.testarea.itext7.direct;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.junit.Test;

import com.google.common.collect.Multimap;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.pdf.ReaderProperties;

/**
 * <p>
 * This test uses the {@link ObjectStructureAnalyzer} to check whether there
 * are multiple indirect objects with the same object number in the example
 * PDFs from https://www.pdf-insecurity.org/signature-shadow/shadow-attacks.html
 * provided as examples for Shadow Attacks. In the hide-and-replace example
 * such objects are observed.
 * </p>
 * @author mklink
 */
public class AnalyzeStructure {

    @Test
    public void testAnalyzeHideAndReplaceShadowFileSigned1() throws IOException {
        System.out.println("\n\nhide-and-replace-shadow-file-signed-1.pdf\n=====\n");
        try (   InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/signature/hide-and-replace-shadow-file-signed-1.pdf");
                ObjectStructureAnalyzer analyzer = new ObjectStructureAnalyzer(new RandomAccessSourceFactory().createSource(resource), new ReaderProperties()); ) {
            Multimap<Integer, ExtPdfIndirectReference> references = analyzer.findIndirectObjects();
            for (Integer num : references.keySet()) {
                Collection<ExtPdfIndirectReference> theseReferences = references.get(num);
                if (theseReferences.size() > 1) {
                    List<Integer> revs = theseReferences.stream().map(ExtPdfIndirectReference::getRevNr).collect(Collectors.toList());
                    boolean suspicious = new TreeSet<Integer>(revs).size() < revs.size();
                    
                    System.out.printf("%d candidates of object %d in revisions %s - %s\n", theseReferences.size(), num, revs, suspicious ? "SUSPECT" : "");
                }
            }
        }
    }

    @Test
    public void testAnalyzeReplaceShadowFileSignedManipulated() throws IOException {
        System.out.println("\n\nreplace-shadow-file-signed-manipulated.pdf\n=====\n");
        try (   InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/signature/replace-shadow-file-signed-manipulated.pdf");
                ObjectStructureAnalyzer analyzer = new ObjectStructureAnalyzer(new RandomAccessSourceFactory().createSource(resource), new ReaderProperties()); ) {
            Multimap<Integer, ExtPdfIndirectReference> references = analyzer.findIndirectObjects();
            for (Integer num : references.keySet()) {
                Collection<ExtPdfIndirectReference> theseReferences = references.get(num);
                if (theseReferences.size() > 1) {
                    List<Integer> revs = theseReferences.stream().map(ExtPdfIndirectReference::getRevNr).collect(Collectors.toList());
                    boolean suspicious = new TreeSet<Integer>(revs).size() < revs.size();
                    
                    System.out.printf("%d candidates of object %d in revisions %s - %s\n", theseReferences.size(), num, revs, suspicious ? "SUSPECT" : "");
                }
            }
        }
    }

}
