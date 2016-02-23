// $Id$
package mkl.testarea.itext7.merge;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

/**
 * This test class tests the {@link PdfDenseMerger}. Just like that class is
 * derived from {@link mkl.testarea.itext5.merge.PdfVeryDenseMergeTool},
 * this test is derived from {@link mkl.testarea.itext5.merge.VeryDenseMerging},
 * the test class of that older merger.
 * 
 * @author mklink
 */
public class DenseMerging
{
    final static File RESULT_FOLDER = new File("target/test-outputs", "merge");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="http://stackoverflow.com/questions/28991291/how-to-remove-whitespace-on-merge">
     * How To Remove Whitespace on Merge
     * </a>
     * <p>
     * Testing {@link PdfDenseMerger} using the OP's files on a even smaller page.
     * </p>
     */
    @Test
    public void testMergeGrandizerFilesA5() throws IOException
    {
        File result = new File(RESULT_FOLDER, "GrandizerMerge-veryDense-A5.pdf");
        
        try (   PdfWriter writer = new PdfWriter(new FileOutputStream(result));
                PdfDocument pdfDocument = new PdfDocument(writer)   )
        {
            PdfDenseMerger pdfMerger = new PdfDenseMerger(pdfDocument);
            pdfMerger.setPageSize(PageSize.A5.rotate()).setTop(18).setBottom(18).setGap(5);

            for (String resourceName : new String[]{"Header.pdf", "Body.pdf", "Footer.pdf"})
            {
                try (   InputStream resource = getClass().getResourceAsStream(resourceName);
                        PdfReader reader = new PdfReader(resource);
                        PdfDocument sourceDocument = new PdfDocument(reader)    )
                {
                    pdfMerger.addPages(sourceDocument, 1, sourceDocument.getNumberOfPages());
                }
            }
        }
    }

    /**
     * <a href="http://stackoverflow.com/questions/28991291/how-to-remove-whitespace-on-merge">
     * How To Remove Whitespace on Merge
     * </a>
     * <p>
     * Testing {@link PdfDenseMerger}.
     * </p>
     */
    @Test
    public void testMergeOnlyGraphics() throws IOException
    {
        byte[] docA = createSimpleCircleGraphicsPdf(20, 20, 20);
        Files.write(new File(RESULT_FOLDER, "circlesOnlyA.pdf").toPath(), docA);
        byte[] docB = createSimpleCircleGraphicsPdf(50, 10, 2);
        Files.write(new File(RESULT_FOLDER, "circlesOnlyB.pdf").toPath(), docB);
        byte[] docC = createSimpleCircleGraphicsPdf(100, -20, 3);
        Files.write(new File(RESULT_FOLDER, "circlesOnlyC.pdf").toPath(), docC);
        byte[] docD = createSimpleCircleGraphicsPdf(20, 20, 20);
        Files.write(new File(RESULT_FOLDER, "circlesOnlyD.pdf").toPath(), docD);

        File result = new File(RESULT_FOLDER, "circlesOnlyMerge-veryDense.pdf");
        
        try (   PdfWriter writer = new PdfWriter(new FileOutputStream(result));
                PdfDocument pdfDocument = new PdfDocument(writer)   )
        {
            PdfDenseMerger pdfMerger = new PdfDenseMerger(pdfDocument);
            pdfMerger.setPageSize(PageSize.A4).setTop(18).setBottom(18).setGap(5);

            for (byte[] bytes : new byte[][]{docA, docB, docC, docD})
            {
                try (   PdfReader reader = new PdfReader(new ByteArrayInputStream(bytes));
                        PdfDocument sourceDocument = new PdfDocument(reader)    )
                {
                    pdfMerger.addPages(sourceDocument, 1, sourceDocument.getNumberOfPages());
                }
            }
        }
    }
    
    static byte[] createSimpleCircleGraphicsPdf(int radius, int gap, int count) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (   PdfWriter writer = new PdfWriter(baos);
                PdfDocument pdfDocument = new PdfDocument(writer)   )
        {
            PdfPage page = pdfDocument.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            float y = page.getPageSize().getTop();
            for (int i = 0; i < count; i++)
            {
                Rectangle pageSize = page.getPageSize();
                if (y <= pageSize.getBottom() + 2*radius)
                {
                    y = pageSize.getTop();
                    canvas.fillStroke();
                    page = pdfDocument.addNewPage();
                    canvas = new PdfCanvas(page);
                }
                canvas.circle((float)(pageSize.getLeft() + pageSize.getWidth() * Math.random()), y-radius, radius);
                y-= 2*radius + gap;
            }
            canvas.fillStroke();
        }

        return baos.toByteArray();
    }
}
