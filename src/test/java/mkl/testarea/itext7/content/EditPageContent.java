package mkl.testarea.itext7.content;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfLiteral;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.RegexBasedLocationExtractionStrategy;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

/**
 * @author mkl
 */
public class EditPageContent
{
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="http://stackoverflow.com/questions/38498431/how-to-remove-filtered-content-from-a-pdf-with-itext">
     * How to remove filtered content from a PDF with iText
     * </a>
     * <br/>
     * <a href="https://1drv.ms/b/s!AmNST-TRoPSemi2k0UnGFsjQM1Yt">
     * document.pdf
     * </a>
     * <p>
     * This test is a port of the iText 5 test area test with the same name.
     * </p>
     * <p>
     * This test shows how to remove text matching the filter condition given
     * by the OP, i.e. any text drawn using a font whose name ends with "BoldMT".
     * </p>
     * <p>
     * This works well, too good actually, as some table headers also use a
     * "BoldMT" font and, therefore, also vanish. As an alternative look at
     * {@link #testRemoveBigTextDocument()} which simply uses the font size
     * as filter condition.
     * </p>
     */
    @Test
    public void testRemoveBoldMTTextDocument() throws IOException
    {
        try (   InputStream resource = getClass().getResourceAsStream("document.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "document-noBoldMTText.pdf"));
                PdfWriter pdfWriter = new PdfWriter(result);
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter) )
        {
            PdfCanvasEditor editor = new PdfCanvasEditor()
            {

                @Override
                protected void write(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands)
                {
                    String operatorString = operator.toString();

                    if (TEXT_SHOWING_OPERATORS.contains(operatorString))
                    {
                        if (getGraphicsState().getFont().getFontProgram().getFontNames().getFontName().endsWith("BoldMT"))
                            return;
                    }
                    
                    super.write(processor, operator, operands);
                }

                final List<String> TEXT_SHOWING_OPERATORS = Arrays.asList("Tj", "'", "\"", "TJ");
            };
            for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++)
            {
                editor.editPage(pdfDocument, i);
            }
        }
    }

    /**
     * <a href="http://stackoverflow.com/questions/38498431/how-to-remove-filtered-content-from-a-pdf-with-itext">
     * How to remove filtered content from a PDF with iText
     * </a>
     * <br/>
     * <a href="https://1drv.ms/b/s!AmNST-TRoPSemi2k0UnGFsjQM1Yt">
     * document.pdf
     * </a>
     * <p>
     * This test is a port of the iText 5 test area test with the same name.
     * </p>
     * <p>
     * This test shows how to remove text filtered by font size. The filter
     * condition given by the OP, i.e. any text drawn using a font whose
     * name ends with "BoldMT", had turned out to match more often than
     * desired, cf. {@link #testRemoveBoldMTTextDocument()}.
     * </p>
     */
    @Test
    public void testRemoveBigTextDocument() throws IOException
    {
        try (   InputStream resource = getClass().getResourceAsStream("document.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "document-noBigText.pdf"));
                PdfWriter pdfWriter = new PdfWriter(result);
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter) )
        {
            PdfCanvasEditor editor = new PdfCanvasEditor()
            {

                @Override
                protected void write(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands)
                {
                    String operatorString = operator.toString();

                    if (TEXT_SHOWING_OPERATORS.contains(operatorString))
                    {
                        if (getGraphicsState().getFontSize() > 100)
                            return;
                    }
                    
                    super.write(processor, operator, operands);
                }

                final List<String> TEXT_SHOWING_OPERATORS = Arrays.asList("Tj", "'", "\"", "TJ");
            };
            for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++)
            {
                editor.editPage(pdfDocument, i);
            }
        }
    }

    /**
     * <a href="http://stackoverflow.com/questions/40401800/traverse-whole-pdf-and-change-some-attribute-with-some-object-in-it-using-itext">
     * Traverse whole PDF and change some attribute with some object in it using iText
     * </a>
     * <p>
     * This test is a port of the iText 5 test area <code>ChangeTextColor</code> test
     * with the same name.
     * </p>
     * <p>
     * This test shows how to change the color of text of a given color. In this case,
     * black text is changed to green.
     * </p>
     * <p>
     * Beware, this is a proof-of-concept, not a final and complete solution. In particular
     * </p>
     * <ul>
     * <li>Text is considered to be black if for its <code>color</code> the expression
     * <code>BaseColor.BLACK.equals(color)</code> is <code>true</code>; as equality among
     * <code>BaseColor</code> and its descendant classes is not completely well-defined,
     * this might lead to some false positives.
     * <li><code>PdfContentStreamEditor</code> only inspects and edits the content
     * stream of the page itself, not the content streams of displayed form xobjects
     * or patterns; thus, some text may not be found.
     * </ul>
     * <p>
     * Improving the class to properly detect black color and to recursively traverse and
     * edit the content streams of used patterns and xobjects remains as an exercise for
     * the reader.
     * </p> 
     */
    @Test
    public void testChangeBlackTextToGreenDocument() throws IOException
    {
        try (   InputStream resource = getClass().getResourceAsStream("document.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "document-blackTextToGreen.pdf"));
                PdfWriter pdfWriter = new PdfWriter(result);
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter) )
        {
            PdfCanvasEditor editor = new PdfCanvasEditor()
            {

                @Override
                protected void write(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands)
                {
                    String operatorString = operator.toString();

                    if (TEXT_SHOWING_OPERATORS.contains(operatorString))
                    {
                        if (currentlyReplacedBlack == null)
                        {
                            Color currentFillColor = getGraphicsState().getFillColor();
                            if (ColorConstants.BLACK.equals(currentFillColor))
                            {
                                currentlyReplacedBlack = currentFillColor;
                                super.write(processor, new PdfLiteral("rg"), Arrays.asList(new PdfNumber(0), new PdfNumber(1), new PdfNumber(0), new PdfLiteral("rg")));
                            }
                        }
                    }
                    else if (currentlyReplacedBlack != null)
                    {
                        if (currentlyReplacedBlack instanceof DeviceCmyk)
                        {
                            super.write(processor, new PdfLiteral("k"), Arrays.asList(new PdfNumber(0), new PdfNumber(0), new PdfNumber(0), new PdfNumber(1), new PdfLiteral("k")));
                        }
                        else if (currentlyReplacedBlack instanceof DeviceGray)
                        {
                            super.write(processor, new PdfLiteral("g"), Arrays.asList(new PdfNumber(0), new PdfLiteral("g")));
                        }
                        else
                        {
                            super.write(processor, new PdfLiteral("rg"), Arrays.asList(new PdfNumber(0), new PdfNumber(0), new PdfNumber(0), new PdfLiteral("rg")));
                        }
                        currentlyReplacedBlack = null;
                    }

                    super.write(processor, operator, operands);
                }

                Color currentlyReplacedBlack = null;

                final List<String> TEXT_SHOWING_OPERATORS = Arrays.asList("Tj", "'", "\"", "TJ");
            };
            for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++)
            {
                editor.editPage(pdfDocument, i);
            }
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/51691625/itext7-how-to-filter-render-events-during-write">
     * itext7 - How to filter render events during write
     * </a>
     * <br/>
     * <a href="https://www.dropbox.com/s/3jh0e4ttr3q1ne9/calendar_2018-08-04_2018-08-19.pdf?dl=0">
     * calendar_2018-08-04_2018-08-19.pdf
     * </a>
     * <p>
     * This test shows how to remove the lines "Created by:" and "Calendar:" from
     * a PDF created from Google Calendar in the Agenda format.
     * </p>
     * <p>
     * Beware, this is a proof-of-concept, not a final and complete solution.
     * </p>
     * <p>
     * In particular it is customized for the OP's use case: This {@link PdfCanvasEditor}
     * only inspects and edits the first level form XObjects of each page because PDFs
     * created from Google Calendar in the Agenda format contain all their page content
     * in a form XObject which in turn is drawn in the page content stream. Furthermore
     * writing is expected to occur parallel to the top of the page.
     * </p>
     * <p>
     * Improving the class for general use remains as an exercise for the reader.
     * </p>
     */
    @Test
    public void testRemoveSpecificLinesCalendar() throws IOException, NoSuchFieldException
    {
        try (   InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/annotate/calendar_2018-08-04_2018-08-19.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfWriter pdfWriter = new PdfWriter(new File(RESULT_FOLDER, "calendar-noTriggerLines.pdf"));
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter) )
        {
            List<Rectangle> triggerRectangles = new ArrayList<>();

            PdfCanvasEditor editor = new PdfCanvasEditor()
            {
                {
                    Field field = PdfCanvasProcessor.class.getDeclaredField("textMatrix");
                    field.setAccessible(true);
                    textMatrixField = field;
                }

                @Override
                protected void nextOperation(PdfLiteral operator, List<PdfObject> operands) {
                    try {
                        recentTextMatrix = (Matrix)textMatrixField.get(this);
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                protected void write(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands)
                {
                    String operatorString = operator.toString();

                    if (TEXT_SHOWING_OPERATORS.contains(operatorString))
                    {
                        Matrix matrix = null;
                        try {
                            matrix = recentTextMatrix.multiply(getGraphicsState().getCtm());
                        } catch (IllegalArgumentException e) {
                            throw new RuntimeException(e);
                        }
                        float y = matrix.get(Matrix.I32);
                        if (triggerRectangles.stream().anyMatch(rect -> rect.getBottom() <= y && y <= rect.getTop())) {
                            if ("TJ".equals(operatorString))
                                operands.set(0, new PdfArray());
                            else
                                operands.set(operands.size() - 2, new PdfString(""));
                        }
                    }
                    
                    super.write(processor, operator, operands);
                }

                final List<String> TEXT_SHOWING_OPERATORS = Arrays.asList("Tj", "'", "\"", "TJ");
                final Field textMatrixField;
                Matrix recentTextMatrix;
            };

            for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++)
            {
                PdfPage page = pdfDocument.getPage(i);
                Set<PdfName> xobjectNames = page.getResources().getResourceNames(PdfName.XObject);
                for (PdfName xobjectName : xobjectNames) {
                    PdfFormXObject xobject = page.getResources().getForm(xobjectName);
                    byte[] content = xobject.getPdfObject().getBytes();
                    PdfResources resources = xobject.getResources();

                    RegexBasedLocationExtractionStrategy regexLocator = new RegexBasedLocationExtractionStrategy("Created by:|Calendar:");
                    new PdfCanvasProcessor(regexLocator).processContent(content, resources);
                    triggerRectangles.clear();
                    triggerRectangles.addAll(regexLocator.getResultantLocations().stream().map(loc -> loc.getRectangle()).collect(Collectors.toSet()));

                    PdfCanvas pdfCanvas = new PdfCanvas(new PdfStream(), resources, pdfDocument);
                    editor.editContent(content, resources, pdfCanvas);
                    xobject.getPdfObject().setData(pdfCanvas.getContentStream().getBytes());
                }
            }
        }
    }
}
