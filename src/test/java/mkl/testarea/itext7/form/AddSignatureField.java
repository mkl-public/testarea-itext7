package mkl.testarea.itext7.form;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfSignatureFormField;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;

/**
 * @author mkl
 */
public class AddSignatureField {
    final static File RESULT_FOLDER = new File("target/test-outputs", "form");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/46906725/adding-a-signature-form-field-to-a-page-its-visible-in-the-last-page-also">
     * Adding a signature form field to a page it's visible in the last page also
     * </a>
     * <p>
     * Indeed, the signature appears on the first and on the last page.
     * This is caused by the use of {@link PdfAcroForm#addField(PdfFormField)}
     * which is documented to add the field on the last page. The OP should use
     * {@link PdfAcroForm#addField(PdfFormField, com.itextpdf.kernel.pdf.PdfPage)}
     * instead.
     * </p>
     */
    @Test
    public void testAddLikeUser1498191() throws FileNotFoundException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new File(RESULT_FOLDER, "pippo.pdf"))))
        {
            //Add some blank pages
            pdfDoc.addNewPage();
            pdfDoc.addNewPage();
            pdfDoc.addNewPage();

            //Instantiate a Signature Form Field using factory
            PdfSignatureFormField sgnField = 
                PdfFormField.createSignature(pdfDoc, new Rectangle(100, 100, 200, 100));

            //setting name and page
            sgnField.setFieldName("pluto");
            sgnField.setPage(1);

            //Adding to AcroForm
            PdfAcroForm.getAcroForm(pdfDoc, true).addField(sgnField);
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/51378902/how-to-add-signature-form-to-an-existing-pdf-using-itext7-so-that-the-output">
     * How to add signature form to an existing pdf (using iText7), so that the output file can be served as a input to pdf (sequential signature)?
     * </a>
     * <p>
     * This test shows how to make the OP's code drawing the table where the fields already are, on the last page.
     * </p>
     */
    @Test
    public void testAddSignaturesInTable() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfWriter pdfWriter = new PdfWriter(new File(RESULT_FOLDER, "test-with-signatures-in-table.pdf"));
                PdfDocument pdfDoc = new PdfDocument(pdfReader, pdfWriter)) {
            pdfDoc.addNewPage();
            Document doc = new Document(pdfDoc);
            Table table = new Table(1);
            table.addCell("Signer 1: Alice");
            table.addCell(createSignatureFieldCell("sig1"));
            table.addCell("Signer 2: Bob");
            table.addCell(createSignatureFieldCell("sig2"));
            table.addCell("Signer 3: Carol");
            table.addCell(createSignatureFieldCell("sig3"));
            doc.add(new AreaBreak(AreaBreakType.LAST_PAGE));
            doc.add(table);
        }
    }

    /**
     * @see #testAddSignaturesInTable()
     */
    protected Cell createSignatureFieldCell(String name) {
        Cell cell = new Cell();
        cell.setHeight(50);
        cell.setWidth(200);
        cell.setNextRenderer(new SignatureFieldCellRenderer(cell, name));
        return cell;
    }

    /**
     * @see #testAddSignaturesInTable()
     */
    class SignatureFieldCellRenderer extends CellRenderer {
        public String name;

        public SignatureFieldCellRenderer(Cell modelElement, String name) {
            super(modelElement);
            this.name = name;
        }

        @Override
        public void draw(DrawContext drawContext) {
            super.draw(drawContext);
            PdfFormField field = PdfFormField.createSignature(drawContext.getDocument(), getOccupiedAreaBBox());
            field.setFieldName(name);
            field.getWidgets().get(0).setHighlightMode(PdfAnnotation.HIGHLIGHT_INVERT);
            field.getWidgets().get(0).setFlags(PdfAnnotation.PRINT);
            PdfAcroForm.getAcroForm(drawContext.getDocument(), true).addField(field);
        }
    }
}
