package mkl.testarea.itext7.content;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.BlockElement;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

/**
 * <a href="http://stackoverflow.com/questions/38663251/itextsharp-7-object-reference-not-set-to-an-instance-of-an-object">
 * iTextSharp 7 object reference not set to an instance of an object
 * </a>
 * <p>
 * This test contains a port of the C# code provided in the stackoverflow question
 * (cf. {@link #testMikesCode()}) and a much simplified version with a single cell
 * (cf. {@link #testSimplified()}) both of which can reproduce the issue.
 * </p>
 * 
 * @author mkl
 */
public class MikesTableIssue
{
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
    }

    @Test
    public void testMikesCode() throws IOException
    {
        float[] tableColumns = { 0.35f, 0.25f, 0.15f, 0.25f };

        try (   FileOutputStream target = new FileOutputStream(new File(RESULT_FOLDER, "mikesTableIssue.pdf"));
                PdfWriter pdfWriter = new PdfWriter(target);
                PdfDocument pdfDocument = new PdfDocument(pdfWriter)    )
        {
            Document document = new Document(pdfDocument);

            Table mainTable = new Table(tableColumns)
                    .setBorder(Border.NO_BORDER)
                    .setWidth(UnitValue.createPercentValue(100))
                    .setHorizontalAlignment(HorizontalAlignment.LEFT)
                    .setPadding(0f);

            for (int i = 0; i < 10; i++)
            {
                addRow(mainTable, "ABCDEFGHIJ", "ABCDEFGHIJ", "ABCDEFGHIJ");
            }

            document.add(mainTable);
        }
    }

    private static void addRow(Table table, String col1, String col2, String col3) throws IOException
    {
        // Label
        addCell(table, col1, true, 1)
            .setBorderTop(new SolidBorder(ColorConstants.BLACK, 0.5f));

        // Product - Voucher and price/pcs        
        addCell(table, col2, true, 1)
            .setBorderTop(new SolidBorder(ColorConstants.BLACK, 0.5f));

        // Message
        addCell(table, col3, true, 2)
            .setBorderTop(new SolidBorder(ColorConstants.BLACK, 0.5f))
            //.SetBorderRight(new iText.Layout.Borders.SolidBorder(iText.Kernel.Colors.Color.BLACK, 0.5f))
            .setHorizontalAlignment(HorizontalAlignment.RIGHT)
            .setTextAlignment(TextAlignment.RIGHT);
    }

    private static Cell addCell(Table table, String text, boolean setBold, int colSpan) throws IOException
    {
        Cell cell = new Cell(1, colSpan)
            .setBorder(Border.NO_BORDER)
            .setVerticalAlignment(VerticalAlignment.BOTTOM);

        if (text != null && text.length() > 0)
        {
            Paragraph paragraph = style(new Paragraph(text));

            if (setBold)
                paragraph.setBold();

            cell.add(paragraph);
        }

        table.addCell(cell);

        return cell;
    }

    public static Paragraph style(BlockElement<Paragraph> element) throws IOException
    {
        element
            .setBorder(Border.NO_BORDER)
            .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
            .setFontSize(10.0f)
            .setFixedLeading(12.0f)
            .setVerticalAlignment(VerticalAlignment.BOTTOM)
            .setMargin(0f);

        return (Paragraph)element;
    }

    @Test
    public void testSimplified() throws IOException
    {
        try (   FileOutputStream target = new FileOutputStream(new File(RESULT_FOLDER, "mikesTableIssueSimple.pdf"));
                PdfWriter pdfWriter = new PdfWriter(target);
                PdfDocument pdfDocument = new PdfDocument(pdfWriter)    )
        {
            Document document = new Document(pdfDocument);

            Table mainTable = new Table(1);

            Cell cell = new Cell()
                    .setBorder(Border.NO_BORDER)
                    //.setBorderRight(new SolidBorder(Color.BLACK, 0.5f))
                    .setBorderTop(new SolidBorder(ColorConstants.BLACK, 0.5f));

            cell.add(new Paragraph("TESCHTINK"));
            
            mainTable.addCell(cell);

            document.add(mainTable);
        }
    }
}
