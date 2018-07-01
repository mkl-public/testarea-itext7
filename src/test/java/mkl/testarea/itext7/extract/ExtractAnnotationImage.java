package mkl.testarea.itext7.extract;

import static com.itextpdf.kernel.pdf.canvas.parser.EventType.RENDER_IMAGE;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.ImageRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;

/**
 * @author mkl
 */
public class ExtractAnnotationImage {
    final static File RESULT_FOLDER = new File("target/test-outputs", "extract");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/51087035/how-to-retrieve-the-image-of-a-pdfstampannotation">
     * How to retrieve the image of a PdfStampAnnotation
     * </a>
     * <br/>
     * add_stamp.pdf (output of the iText test com.itextpdf.samples.sandbox.annotations.AddStamp)
     * <p>
     * This test shows how to extract images used in annotation appearances.
     * Beware: This is a very simple extractor, it does not check whether the
     * image really is visible in the annotation, and it ignores any masks,
     * i.e. transparency.
     * </p>
     */
    @Test
    public void testExtractFromAddStamp() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("add_stamp.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfDocument pdfDocument = new PdfDocument(pdfReader)    ) {
            saveAnnotationImages(pdfDocument, new File(RESULT_FOLDER, "add_stamp").getPath());
        }
    }

    void saveAnnotationImages(PdfDocument pdfDocument, String prefix) throws IOException {
        for (int pageNumber = 1; pageNumber <= pdfDocument.getNumberOfPages(); pageNumber++) {
            PdfPage page = pdfDocument.getPage(pageNumber);
            int index = 0;
            for (PdfAnnotation annotation : page.getAnnotations()) {
                PdfDictionary normal = annotation.getAppearanceObject(PdfName.N);
                if (normal instanceof PdfStream) {
                    Map<byte[], String> images = extractAnnotationImages((PdfStream)normal);
                    for (Map.Entry<byte[], String> entry : images.entrySet()) {
                        Files.write(new File(String.format("%s-%s-%s.%s", prefix, pageNumber, index++, entry.getValue())).toPath(), entry.getKey());
                    }
                }
            }
        }
    }

    Map<byte[], String> extractAnnotationImages(PdfStream xObject) {
        final Map<byte[], String> result = new HashMap<>();
        IEventListener renderListener = new IEventListener() {
            @Override
            public Set<EventType> getSupportedEvents() {
                return Collections.singleton(RENDER_IMAGE);
            }
            
            @Override
            public void eventOccurred(IEventData data, EventType type) {
                if (data instanceof ImageRenderInfo) {
                    ImageRenderInfo imageRenderInfo = (ImageRenderInfo) data;
                    byte[] bytes = imageRenderInfo.getImage().getImageBytes();
                    String extension = imageRenderInfo.getImage().identifyImageFileExtension();
                    result.put(bytes, extension);
                }
            }
        };

        PdfCanvasProcessor processor = new PdfCanvasProcessor(renderListener, Collections.emptyMap());
        processor.processContent(xObject.getBytes(), new PdfResources(xObject.getAsDictionary(PdfName.Resources)));

        return result;
    }
}
