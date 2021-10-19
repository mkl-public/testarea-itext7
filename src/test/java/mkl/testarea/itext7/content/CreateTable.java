package mkl.testarea.itext7.content;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;

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
            table.addCell(new Cell().add(new Paragraph("C1")));
            table.addCell(new Cell().add(new Paragraph("C2")));
            /*
            for (TaxTypeModel taxType : sale.getTaxModel().getTaxTypeModelList()) {
                table.addCell(new Cell().add(new Paragraph("C9")));
                table.addCell(new Cell().add(new Paragraph("C10")));
            }
            */
            table.addCell(new Cell().add(new Paragraph("C3")));
            table.addCell(new Cell().add(new Paragraph("C4")));

            table.addCell(new Cell().add(new Paragraph("C5")));
            table.addCell(new Cell().add(new Paragraph("C6")));

            table.addCell(new Cell().add(new Paragraph("C7")));
            table.addCell(new Cell().add(new Paragraph("C8")));

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

    /**
     * <a href="https://stackoverflow.com/questions/50196083/itext-7-0-2-adding-fixed-size-table-to-document-text-not-wrapping-and-somethimes">
     * iText 7.0.2 adding fixed size table to document text not wrapping and somethimes goes into infinite loop
     * </a>
     * <p>
     * This is the OP's original code with follow-ups to the API changes
     * from iText 7.0.2 to 7.1.x. It now runs without issue.
     * </p>
     */
    @Test
    public void testCreateFixedSizeTableLikeKPatel() throws IOException {
        PdfWriter pdfWriter = new PdfWriter(new File(RESULT_FOLDER, "fixedSizeTableLikeKPatel.pdf"));
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);   
        Document document = new Document(pdfDocument);
        document.setLeftMargin(30);
        document.setRightMargin(30);

        final Table myTable = new Table(new float[]
                { 30, 65, 120, 180, 100, 25 });// { 30, 65, 150, 180, 100, 25 }); works.
        setTableProperties(myTable, pdfDocument, 70, 7, "italic", ColorConstants.BLACK);
        myTable.setFixedLayout();

        final PdfFont font = getPdfFont(StandardFonts.HELVETICA);
        myTable.setFont(font).setFontSize(7).setItalic().setFontColor(ColorConstants.WHITE);

        //*** Start - Added Header*****
        //BLOCK THIS TO See Issue 1 below, UNBLOCK Causes issue 2 and it hangs
        createTableHeader(myTable);
        //*** End - Added Header *****

        //*** Start Add Data ****
        equipmentTableData(myTable, "NUMITEMS", false, TextAlignment.CENTER);
        equipmentTableData(myTable, "CONDENSADORCONDENSAD", false, TextAlignment.CENTER); 

        // MATNUMBERUCACHBMPVAIRD4EFANFUC (com.itextpdf.layout.renderer.RootRenderer - Element does not fit current area)
        // MATNUMBERUCACHB-PVAIRD4EFANFUC (Works ok.. it wraps at '-')
        // MATNUMBERUCACH1MPVAIRD4EFANFUC (added '1' in middle Works ok.. 'MATNUMBERUCACH1MPVAIRD4EF' 'ANFUC')
        equipmentTableData(myTable, "MATNUMBERUCACHBMPVAIRD4EFANFUC", false, TextAlignment.CENTER);
        equipmentTableData(myTable, "MATERIALDESCRIPTIONDOPKSOPKDKJSLIOULOISOOPWOSKLISL", false, TextAlignment.CENTER);
        equipmentTableData(myTable, "DETAILS", false, TextAlignment.CENTER);
        equipmentTableData(myTable, "QTY", false, TextAlignment.CENTER);
        //*** End Add Data ****

        document.add(myTable); // Goes in infinite loop if createTableHeader is enabled above, and i can see pdf file size growing

        document.close(); 
    }

    /**
     * @see #testCreateFixedSizeTableLikeKPatel()
     */
    private static Table createTableHeader(final Table myTable)
    {
        Cell customCell;
        List listforPrepared;
        listforPrepared = new List().setSymbolIndent(0).setListSymbol(" ");
        listforPrepared.add("Line");
        listforPrepared.add("Item");

        customCell = new Cell(1, 1).add(listforPrepared).setBackgroundColor(ColorConstants.GRAY).setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER);
        myTable.addHeaderCell(customCell);

        listforPrepared = new List().setSymbolIndent(0).setListSymbol(" ");
        listforPrepared.add("Tag:");
        customCell = new Cell(1, 1).add(listforPrepared).setBackgroundColor(ColorConstants.GRAY).setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER);
        myTable.addHeaderCell(customCell);

        listforPrepared = new List().setSymbolIndent(0).setListSymbol(" ");
        listforPrepared.add("Material");
        listforPrepared.add("Number");
        customCell = new Cell(1, 1).add(listforPrepared).setBackgroundColor(ColorConstants.GRAY).setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER);
        myTable.addHeaderCell(customCell);

        listforPrepared = new List().setSymbolIndent(0).setListSymbol(" ");
        listforPrepared.add("Description");
        customCell = new Cell(1, 1).add(listforPrepared).setBackgroundColor(ColorConstants.GRAY).setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER);
        myTable.addHeaderCell(customCell);

        listforPrepared = new List().setSymbolIndent(0).setListSymbol(" ");
        listforPrepared.add("Detail");
        customCell = new Cell(1, 1).add(listforPrepared).setBackgroundColor(ColorConstants.GRAY).setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER);
        myTable.addHeaderCell(customCell);

        listforPrepared = new List().setSymbolIndent(0).setListSymbol(" ");
        listforPrepared.add("QTY");
        customCell = new Cell(1, 1).add(listforPrepared).setBackgroundColor(ColorConstants.GRAY).setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER);
        myTable.addHeaderCell(customCell);

        return myTable;
    }

    /**
     * @see #testCreateFixedSizeTableLikeKPatel()
     */
    private static  Table equipmentTableData(final Table Table, final String data, final boolean isGrey, final TextAlignment textAligned)
    {

        final PdfFont helvetica = getPdfFont(StandardFonts.HELVETICA);
        final Text dataText = new Text((data)).setFont(helvetica).setFontSize(7).setItalic()
                .setFontColor(ColorConstants.BLACK); // new Font(Font.FontFamily.HELVETICA, 7, Font.ITALIC);
        final Cell cellData = new Cell(1, 1).add(new Paragraph(dataText));
        cellData.setTextAlignment(textAligned);

        if (isGrey)
        {
            cellData.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        }
        else
        {
            cellData.setBackgroundColor(ColorConstants.WHITE);
        }
        cellData.setPadding(0f);
        Table.addCell(cellData);
        return Table;
    }

    /**
     * @see #testCreateFixedSizeTableLikeKPatel()
     */
    public static void setTableProperties(Table tblName, PdfDocument pdf, int widthSize, int fontSize, String fontStyle, Color fontColor)
    {
        tblName.setWidth(pdf.getDefaultPageSize().getWidth() - widthSize); /* Set Width of a Table */
        tblName.setFontColor(fontColor); /* Set Font Color */

        if (fontStyle.toLowerCase() == "italic")
            tblName.setFontSize(fontSize).setItalic();
        else if (fontStyle.toLowerCase() == "bold")
            tblName.setFontSize(fontSize).setBold();
        else if (fontStyle.toLowerCase() == "bold-italic")
            tblName.setFontSize(fontSize).setBold().setItalic();
    }

    /**
     * @see #testCreateFixedSizeTableLikeKPatel()
     */
    public static PdfFont getPdfFont(final String font)
    {
        try
        {
            return PdfFontFactory.createFont(font);
        }
        catch (final Exception e)
        {
            System.out.println("Error " + e);
        }
        return null;
    }

    /**
     * <a href="https://stackoverflow.com/questions/53449312/itext-divide-cell-horizontal">
     * iText divide cell horizontal
     * </a>
     * <p>
     * This test shows how to construct a table as desired by the OP.
     * </p>
     */
    @Test
    public void testCreateTableForDennis() throws IOException {
        try (   OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "tableForDennis.pdf"));
                PdfWriter writer = new PdfWriter(result);
                PdfDocument pdfDocument = new PdfDocument(writer);
                Document doc = new Document(pdfDocument)   )
        {
            Table table = new Table(new float[] {30, 30, 30, 30, 30, 30, 30, 30, 30});

            for (int i = 0; i < 4; i++) {
                table.addCell(new Cell(2, 1).add(new Paragraph("Text")));
                table.addCell(new Cell(2, 1).add(new Paragraph("Text")));
                table.addCell(new Cell().setHeight(15));
                table.addCell(new Cell(2, 1).add(new Paragraph("Text")));
                table.addCell(new Cell().setHeight(15));
                table.addCell(new Cell(2, 1).add(new Paragraph("Text")));
                table.addCell(new Cell().setHeight(15));
                table.addCell(new Cell(2, 1).add(new Paragraph("Text")));
                table.addCell(new Cell().setHeight(15));
                
                table.addCell(new Cell().setHeight(15));
                table.addCell(new Cell().setHeight(15));
                table.addCell(new Cell().setHeight(15));
                table.addCell(new Cell().setHeight(15));
            }

            doc.add(table);
            doc.close();
        }
    }
}
