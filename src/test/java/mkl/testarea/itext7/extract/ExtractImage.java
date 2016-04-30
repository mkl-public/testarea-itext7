// $Id$
package mkl.testarea.itext7.extract;

import static com.itextpdf.kernel.pdf.canvas.parser.EventType.RENDER_IMAGE;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.PdfDocumentContentParser;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.ImageRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;

/**
 * @author mkl
 */
public class ExtractImage
{
    final static File RESULT_FOLDER = new File("target/test-outputs", "extract");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="http://stackoverflow.com/questions/36936524/itextsharp-extracted-cmyk-image-is-inverted">
     * iTextSharp: Extracted CMYK Image is inverted
     * </a>
     * <br/>
     * <a href="http://docdro.id/ZoHmiAd">sampleCMYK.pdf</a>
     * <p>
     * The issue is just the same in iText 7.
     * </p>
     */
    @Test
    public void testExtractCmykImage() throws IOException
    {
        try  (InputStream resourceStream = getClass().getResourceAsStream("sampleCMYK.pdf") )
        {
            PdfReader reader = new PdfReader(resourceStream);
            PdfDocument document = new PdfDocument(reader);
            PdfDocumentContentParser contentParser = new PdfDocumentContentParser(document);
            for (int page = 1; page <= document.getNumberOfPages(); page++)
            {
                contentParser.processContent(page, new IEventListener()
                {
                    @Override
                    public Set<EventType> getSupportedEvents()
                    {
                        return Collections.singleton(RENDER_IMAGE);
                    }
                    
                    @Override
                    public void eventOccurred(IEventData data, EventType type)
                    {
                        if (data instanceof ImageRenderInfo)
                        {
                            ImageRenderInfo imageRenderInfo = (ImageRenderInfo) data;
                            byte[] bytes = imageRenderInfo.getImage().getImageBytes();
                            try
                            {
                                Files.write(new File(RESULT_FOLDER, "sampleCMYK-" + index++ + ".img").toPath(), bytes);
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                    
                    int index = 0;
                });
            }
        }
    }
}
