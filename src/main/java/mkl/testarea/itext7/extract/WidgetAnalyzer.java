package mkl.testarea.itext7.extract;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.io.source.IRandomAccessSource;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy;

/**
 * <p>
 * This class inspects form fields and determines both their actual values
 * and the values contained in their widgets.
 * </p>
 * <p>
 * It is in particular designed for detecting PDFs that might be used
 * in replace Shadow Attacks as proposed by the Ruhr Uni Bochum, see
 * https://www.pdf-insecurity.org/signature-shadow/shadow-attacks.html
 * </p>
 * 
 * @author mkl
 */
public class WidgetAnalyzer implements Closeable {
    public WidgetAnalyzer(IRandomAccessSource byteSource, ReaderProperties properties) throws IOException {
        PdfReader pdfReader = new PdfReader(byteSource, properties);
        pdfReader.setUnethicalReading(true);
        pdfDocument = new PdfDocument(pdfReader);
    }

    @Override
    public void close() {
        pdfDocument.close();
    }

    public Map<String, FieldValues<String>> findFieldValues() {
        Map<String, FieldValues<String>> fieldValues = new HashMap<String, FieldValues<String>>();

        PdfAcroForm pdfAcroForm = PdfAcroForm.getAcroForm(pdfDocument, false);
        if (pdfAcroForm != null) {
            for (Map.Entry<String, PdfFormField> entry : pdfAcroForm.getFormFields().entrySet()) {
                String name = entry.getKey();
                PdfFormField field = entry.getValue();
                if (field instanceof PdfTextFormField) {
                    PdfTextFormField textField = (PdfTextFormField) field;
                    String actualValue = textField.getValueAsString();
                    Set<String> widgetTexts = new TreeSet<String>();
                    for (PdfWidgetAnnotation widget : textField.getWidgets()) {
                        PdfDictionary normal = widget.getNormalAppearanceObject();
                        if (normal instanceof PdfStream) {
                            String widgetText = extractText((PdfStream) normal);
                            widgetTexts.add(widgetText);
                        }
                    }
                    fieldValues.put(name, new FieldValues<String>(name, actualValue, widgetTexts));
                }
            }
        }

        return fieldValues;
    }

    String extractText(PdfStream appearance) {
        PdfDictionary resourceDictionary = appearance.getAsDictionary(PdfName.Resources);
        PdfResources resources = resourceDictionary != null ? new PdfResources(resourceDictionary) : new PdfResources();
        ITextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
        PdfCanvasProcessor parser = new PdfCanvasProcessor(strategy, Collections.emptyMap());
        parser.processContent(appearance.getBytes(), resources);
        return strategy.getResultantText();
    }

    final PdfDocument pdfDocument;
}
