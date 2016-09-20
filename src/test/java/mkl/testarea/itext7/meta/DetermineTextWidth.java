package mkl.testarea.itext7.meta;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

/**
 * @author mkl
 */
public class DetermineTextWidth
{
    final static File RESULT_FOLDER = new File("target/test-outputs", "meta");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="http://stackoverflow.com/questions/39577231/how-to-simulate-width-of-an-text-element-before-actual-render-with-itextsharp-ve">
     * How to simulate width of an text element before actual render with iTextSharp version 7
     * </a>
     * <p>
     * This test demonstrates how one can calculate the width text would have for
     * a given font and font size. This code does assume no special effects to be
     * in place, in particular no extra character or word spacing and no kerning.
     * </p>
     */
    @Test
    public void testDetermineSimpleTextWidth() throws IOException
    {
        String text = "Portable Document Format";
        float fontSize = 12;
        try ( InputStream resource = getClass().getResourceAsStream("Lato-Regular.ttf") )
        {
            byte[] resourceBytes = StreamUtil.inputStreamToArray(resource);
            PdfFont font = PdfFontFactory.createFont(resourceBytes, PdfEncodings.IDENTITY_H);
            GlyphLine glyphLine = font.createGlyphLine(text);

            int width = 0;
            for (int i = 0; i < glyphLine.size(); i++)
            {
                Glyph glyph = glyphLine.get(i);
                width += glyph.getWidth();
            }

            float userSpaceWidth = width * fontSize / 1000.0f;

            System.out.printf("The width of '%s' is %s text space units.\n", text, width);
            System.out.printf("For a font size %s, therefore, this is %s unscaled user space units.\n", fontSize, userSpaceWidth);

            try (   PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(new File(RESULT_FOLDER, "simpleTextWidth.pdf"))));
                    Document doc = new Document(pdfDoc) )
            {
                Paragraph p = new Paragraph(text).setFont(font).setFontSize(fontSize);
                doc.add(p);

                PdfCanvas canvas = new PdfCanvas(pdfDoc, 1);
                Rectangle pageSize = pdfDoc.getPage(1).getPageSize();
                canvas.moveTo(pageSize.getLeft() + doc.getLeftMargin(), pageSize.getBottom());
                canvas.lineTo(pageSize.getLeft() + doc.getLeftMargin(), pageSize.getTop());
                canvas.moveTo(pageSize.getLeft() + doc.getLeftMargin() + userSpaceWidth, pageSize.getBottom());
                canvas.lineTo(pageSize.getLeft() + doc.getLeftMargin() + userSpaceWidth, pageSize.getTop());
                canvas.stroke();
            }
        }
    }

}
