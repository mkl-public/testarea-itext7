package mkl.testarea.itext7.form;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

/**
 * @author mkl
 */
public class AddFormFieldWeirdnesses {
    final static File RESULT_FOLDER = new File("target/test-outputs", "form");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * Given a two-page PDF in which the pages share an indirect object
     * containing an empty array as annotations array. As the array is
     * empty, this is not an invalid structure. Furthermore, it can
     * realistically turn up as a result of an optimization.
     * 
     * Adding a form field to the second page of that PDF now results
     * in the annotation of that field being present on both pages.
     * This is neither valid nor desired.
     */
    @Test
    public void testSharedEmptyAnnotationArray() throws IOException {
        File emptyArrayFile = new File(RESULT_FOLDER, "SharedEmptyAnnotationArray.pdf");
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(emptyArrayFile))) {
            PdfArray annotations = new PdfArray();
            annotations.makeIndirect(pdfDocument);
            PageSize pageSize = new PageSize(400, 400);

            PdfPage pdfPage = pdfDocument.addNewPage(pageSize);
            pdfPage.put(PdfName.Annots, annotations);
            PdfCanvas pdfCanvas = new PdfCanvas(pdfPage);
            pdfCanvas.beginText()
                     .setFontAndSize(PdfFontFactory.createFont(), 100)
                     .moveText(100, 150)
                     .showText("1")
                     .endText();

            pdfPage = pdfDocument.addNewPage(pageSize);
            pdfPage.put(PdfName.Annots, annotations);
            pdfCanvas = new PdfCanvas(pdfPage);
            pdfCanvas.beginText()
                     .setFontAndSize(PdfFontFactory.createFont(), 100)
                     .moveText(100, 150)
                     .showText("2")
                     .endText();
        }

        File fileWithField = new File(RESULT_FOLDER, "SharedEmptyAnnotationArray-withField.pdf");
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(emptyArrayFile), new PdfWriter(fileWithField))) {
            Rectangle rectangle = new Rectangle(50, 50, 300, 300);
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDocument, true);
            PdfTextFormField pdfTextFormField = PdfFormField.createText(pdfDocument, rectangle, "Field");
            form.addField(pdfTextFormField); // This method adds the field to the last page in the document
            pdfTextFormField.setValue("Test");
        }
    }

}
