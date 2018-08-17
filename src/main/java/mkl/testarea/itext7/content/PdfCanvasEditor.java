package mkl.testarea.itext7.content;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfLiteral;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfOutputStream;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.IContentOperator;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;

/**
 * <a href="http://stackoverflow.com/questions/40951776/manipulate-paths-color-etc-in-itext">
 * Manipulate paths, color etc. in iText
 * </a>
 * <p>
 * This class presents a simple content stream editing framework. As is it creates an equivalent
 * copy of the original page content stream. To actually edit, simply overwrite the method
 * {@link #write(PdfCanvasProcessor, PdfLiteral, List)} to not (as in this class) write
 * the given operations as they are but change them in some fancy way.
 * </p>
 * <p>
 * This is a port of the iText 5 test area class <code>PdfContentStreamEditor</code>.
 * </p>
 * 
 * @author mkl
 */
public class PdfCanvasEditor extends PdfCanvasProcessor
{
    /**
     * This method edits the immediate contents of a page, i.e. its content stream.
     * It explicitly does not descent into form xobjects, patterns, or annotations.
     */
    public void editPage(PdfDocument pdfDocument, int pageNumber) throws IOException
    {
        if ((pdfDocument.getReader() == null) || (pdfDocument.getWriter() == null))
        {
            throw new PdfException("PdfDocument must be opened in stamping mode.");
        }

        PdfPage page = pdfDocument.getPage(pageNumber);
        PdfResources pdfResources = page.getResources();
        PdfCanvas pdfCanvas = new PdfCanvas(new PdfStream(), pdfResources, pdfDocument);
        editContent(page.getContentBytes(), pdfResources, pdfCanvas);
        page.put(PdfName.Contents, pdfCanvas.getContentStream());
    }

    /**
     * This method processes the content bytes and outputs to the given canvas.
     * It explicitly does not descent into form xobjects, patterns, or annotations.
     */
    public void editContent(byte[] contentBytes, PdfResources resources, PdfCanvas canvas)
    {
        this.canvas = canvas;
        processContent(contentBytes, resources);
        this.canvas = null;
    }

    /**
     * <p>
     * This method retrieves the next operation before its original {@link IContentOperator}
     * is called. The default does nothing.
     * </p>
     * <p>
     * Override this method to retrieve state information from before the
     * operation execution.
     * </p> 
     */
    protected void nextOperation(PdfLiteral operator, List<PdfObject> operands) {
        
    }

    /**
     * <p>
     * This method writes content stream operations to the target canvas. The default
     * implementation writes them as they come, so it essentially generates identical
     * copies of the original instructions the {@link ContentOperatorWrapper} instances
     * forward to it.
     * </p>
     * <p>
     * Override this method to achieve some fancy editing effect.
     * </p> 
     */
    protected void write(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands)
    {
        PdfOutputStream pdfOutputStream = canvas.getContentStream().getOutputStream();
        int index = 0;

        for (PdfObject object : operands)
        {
            pdfOutputStream.write(object);
            if (operands.size() > ++index)
                pdfOutputStream.writeSpace();
            else
                pdfOutputStream.writeNewLine();
        }
    }

    //
    // constructor giving the parent a dummy listener to talk to 
    //
    public PdfCanvasEditor()
    {
        super(new DummyEventListener());
    }

    //
    // Overrides of PdfContentStreamProcessor methods
    //
    @Override
    public IContentOperator registerContentOperator(String operatorString, IContentOperator operator)
    {
        ContentOperatorWrapper wrapper = new ContentOperatorWrapper();
        wrapper.setOriginalOperator(operator);
        IContentOperator formerOperator = super.registerContentOperator(operatorString, wrapper);
        return formerOperator instanceof ContentOperatorWrapper ? ((ContentOperatorWrapper)formerOperator).getOriginalOperator() : formerOperator;
    }

    //
    // members holding the output canvas and the resources
    //
    protected PdfCanvas canvas = null;

    //
    // A content operator class to wrap all content operators to forward the invocation to the editor
    //
    class ContentOperatorWrapper implements IContentOperator
    {
        public IContentOperator getOriginalOperator()
        {
            return originalOperator;
        }

        public void setOriginalOperator(IContentOperator originalOperator)
        {
            this.originalOperator = originalOperator;
        }

        @Override
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands)
        {
            nextOperation(operator, operands);
            if (originalOperator != null && !"Do".equals(operator.toString()))
            {
                originalOperator.invoke(processor, operator, operands);
            }
            write(processor, operator, operands);
        }

        private IContentOperator originalOperator = null;
    }

    //
    // A dummy event listener to give to the underlying canvas processor to feed events to
    //
    static class DummyEventListener implements IEventListener
    {
        @Override
        public void eventOccurred(IEventData data, EventType type)
        { }

        @Override
        public Set<EventType> getSupportedEvents()
        {
            return null;
        }
    }
}
