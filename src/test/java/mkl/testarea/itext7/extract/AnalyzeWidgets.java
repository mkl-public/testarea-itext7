package mkl.testarea.itext7.extract;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.pdf.ReaderProperties;

/**
 * <p>
 * This test uses the {@link WidgetAnalyzer} to check whether there are form
 * fields with widget texts differing from their actual values in the example
 * PDFs from https://www.pdf-insecurity.org/signature-shadow/shadow-attacks.html
 * provided as examples for Shadow Attacks. In the replace example such objects
 * are observed.
 * </p>
 * @author mkl
 */
public class AnalyzeWidgets {

    @Test
    public void test() throws IOException {
        System.out.println("\n\nreplace-shadow-file.pdf\n=====\n");
        try (InputStream resource = getClass().getResourceAsStream("replace-shadow-file.pdf")) {
            analyzePdf(resource);
        }
    }

    public void analyzePdf(InputStream pdfStream) throws IOException {
        try (WidgetAnalyzer analyzer = new WidgetAnalyzer(new RandomAccessSourceFactory().createSource(pdfStream), new ReaderProperties())) {
            Map<String, FieldValues<String>> fieldValues = analyzer.findFieldValues();
            for (Map.Entry<String, FieldValues<String>> entry : fieldValues.entrySet()) {
                FieldValues<String> values = entry.getValue();
                Set<String> widgetValues = values.getWidgetValues();
                boolean suspicious = widgetValues.size() > 1 || (widgetValues.size() == 1 && !values.getActualValue().equals(widgetValues.iterator().next()));
                System.out.printf("Field %s has actual value '%s' and widget values %s - %s\n", entry.getKey(), values.getActualValue(), widgetValues, suspicious ? "SUSPECT" : "");
            }
        }
    }

}
