package mkl.testarea.itext7.content;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceCmyk;
import com.itextpdf.kernel.color.DeviceGray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfLiteral;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;

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
                            if (Color.BLACK.equals(currentFillColor))
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
}
