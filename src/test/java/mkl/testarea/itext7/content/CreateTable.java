package mkl.testarea.itext7.content;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;

/**
 * @author mkl
 */
public class CreateTable
{
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="http://stackoverflow.com/questions/40987068/itext-7-table-content-is-not-getting-displayed">
     * IText 7: Table Content is not getting displayed
     * </a>
     * <p>
     * Cannot reproduce for original code, all cell values appear. 
     * </p>
     */
    @Test
    public void testCreateSimpleTableLikeRahulDevMishra() throws IOException
    {
        try (   OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "simpleTableRahulDevMishra.pdf"));
                PdfWriter writer = new PdfWriter(result);
                PdfDocument pdfDocument = new PdfDocument(writer);
                Document doc = new Document(pdfDocument)   )
        {
            Table table = new Table(2, true);
            table.addCell(new Cell().add("C1"));
            table.addCell(new Cell().add("C2"));
            /*
            for (TaxTypeModel taxType : sale.getTaxModel().getTaxTypeModelList()) {
                table.addCell(new Cell().add("C9"));
                table.addCell(new Cell().add("C10"));
            }
            */
            table.addCell(new Cell().add("C3"));
            table.addCell(new Cell().add("C4"));

            table.addCell(new Cell().add("C5"));
            table.addCell(new Cell().add("C6"));

            table.addCell(new Cell().add("C7"));
            table.addCell(new Cell().add("C8"));

            doc.add(table);
            table.complete();
            doc.close();
        }
    }

    /**
     * <a href="http://stackoverflow.com/questions/40987068/itext-7-table-content-is-not-getting-displayed">
     * IText 7: Table Content is not getting displayed
     * </a>
     * <p>
     * Cannot reproduce for additional code from first edit, all cell values appear. 
     * </p>
     */
    @Test
    public void testCreateSimpleTable2LikeRahulDevMishra() throws IOException
    {
        try (   OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "simpleTable2RahulDevMishra.pdf"));
                PdfWriter writer = new PdfWriter(result);
                PdfDocument pdfDocument = new PdfDocument(writer);
                Document doc = new Document(pdfDocument)   )
        {
            Table table = new Table(8);
            for (int i = 0; i < 16; i++) {
                table.addCell("hi");
            }
            doc.add(table);
            doc.close();
        }
    }
}
