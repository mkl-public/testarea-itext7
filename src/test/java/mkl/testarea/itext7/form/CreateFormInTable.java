package mkl.testarea.itext7.form;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;

/**
 * @author mkl
 */
public class CreateFormInTable {
    final static File RESULT_FOLDER = new File("target/test-outputs", "form");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/47594075/itext-7-0-5-forms-nullpointerexception-when-adding-a-lot-of-tables-with-form">
     * iText 7.0.5 - Forms - NullPointerException when adding a lot of tables with form fields
     * </a>
     * <p>
     * Indeed, the issue can be reproduced.
     * </p>
     */
    @Test
    public void testCreateFormInTable() throws FileNotFoundException {
        manipulatePdf(new File(RESULT_FOLDER, "TableLikeTobiasRettstadt.pdf").getAbsolutePath());
    }

    /**
     * @see #testCreateFormInTable()
     */
    protected void manipulatePdf(String dest) throws FileNotFoundException  {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
        Document doc = new Document(pdfDoc);
 
        for (int i = 0; i < 50; i++) {
            System.out.println(i);
            Table table = new Table(2);
            Cell cell;
            cell = new Cell().add("Name:");
            table.addCell(cell);
            cell = new Cell();
            cell.setNextRenderer(new MyCellRenderer(cell, "name" + i));
            table.addCell(cell);
            cell = new Cell().add("Address");
            table.addCell(cell);
            cell = new Cell();
            cell.setNextRenderer(new MyCellRenderer(cell, "address" + i));
            table.addCell(cell);
            doc.add(table);
        }
        doc.close();
    }
 
    /**
     * <a href="https://stackoverflow.com/questions/47594075/itext-7-0-5-forms-nullpointerexception-when-adding-a-lot-of-tables-with-form">
     * iText 7.0.5 - Forms - NullPointerException when adding a lot of tables with form fields
     * </a>
     * <p>
     * The original issue reproduced in {@link #testCreateFormInTable()}
     * can much easier be reproduced like this.
     * </p>
     * <p>
     * The cause of the issue is that for a form field (widget) without
     * an associated page, iText tries to determine the page that widget
     * is on by iterating all document pages and looking up their respective
     * annotation arrays. 
     * </p>
     * <p>
     * In the code above, though, the previous newly created pages, at
     * least the first one, has already been flushed to the writer and
     * de-initialized in memory. Thus, the loop over the pages tries to
     * retrieve a value from a de-initialized page dictionary, i.e. from
     * a <code>null</code> {@link Map}, hence the {@link NullPointerException}.
     * </p>
     */
    @Test
    public void testCreateFormInTableVariant() throws FileNotFoundException {
        manipulatePdfVariant(new File(RESULT_FOLDER, "TableLikeTobiasRettstadtVariant.pdf").getAbsolutePath());
    }

    /**
     * @see #testCreateFormInTableVariant()
     */
    protected void manipulatePdfVariant(String dest) throws FileNotFoundException  {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
        Document doc = new Document(pdfDoc);
        doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
 
        PdfTextFormField field = PdfFormField.createText(pdfDoc, new Rectangle(100, 100, 300, 20), "name", "");
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        field.setPage(pdfDoc.getNumberOfPages());
        field.getWidgets().get(0).setPage(pdfDoc.getLastPage());
        form.addField(field);

        doc.close();
    }

    /**
     * @see #testCreateFormInTable()
     */
    private class MyCellRenderer extends CellRenderer {
        protected String fieldName;
 
        public MyCellRenderer(Cell modelElement, String fieldName) {
            super(modelElement);
            this.fieldName = fieldName;
        }
 
        @Override
        public void draw(DrawContext drawContext) {
            super.draw(drawContext);
            PdfTextFormField field = PdfFormField.createText(drawContext.getDocument(), getOccupiedAreaBBox(), fieldName, "");
            PdfAcroForm form = PdfAcroForm.getAcroForm(drawContext.getDocument(), true);
            form.addField(field);
        }
    }
}
