package mkl.testarea.itext7.content;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfLiteral;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;

/**
 * @author mkl
 */
public class RemoveGraphicsByColor {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/58029533/traverse-whole-pdf-and-remove-underlines-of-hyperlinks-annotations-only-itex">
     * Traverse whole PDF and Remove underlines of hyperlinks (annotations) only + iText
     * </a>
     * <br/>
     * <a href="https://raad-dev-test.s3.ap-south-1.amazonaws.com/36/2019-08-30/Control_of_nitrosamine_impurities_in_sartans__rev.pdf">
     * Control_of_nitrosamine_impurities_in_sartans__rev.pdf
     * </a>
     * <p>
     * This test uses the {@link PdfGraphicsRemoverByColor} to remove
     * blue underlines from the given document. This content editor
     * actually drops any path stroking or filling of a given color.
     * </p>
     * @throws IOException
     */
    @Test
    public void testRemoveBlueLinesFromControlOfNitrosamineImpuritiesInSartansRev() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("Control_of_nitrosamine_impurities_in_sartans__rev.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "Control_of_nitrosamine_impurities_in_sartans__rev-RemoveBlueLines.pdf"));
                PdfWriter pdfWriter = new PdfWriter(result);
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter) )
        {
            PdfCanvasEditor editor = new PdfGraphicsRemoverByColor(ColorConstants.BLUE);
            for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++)
            {
                editor.editPage(pdfDocument, i);
            }
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/58029533/traverse-whole-pdf-and-remove-underlines-of-hyperlinks-annotations-only-itex">
     * Traverse whole PDF and Remove underlines of hyperlinks (annotations) only + iText
     * </a>
     * <br/>
     * <a href="https://raad-dev-test.s3.ap-south-1.amazonaws.com/36/2019-08-30/EDQM_reports_issues_of_non-compliance_with_tooth__Mac.pdf">
     * EDQM_reports_issues_of_non-compliance_with_tooth__Mac.pdf
     * </a>
     * <p>
     * This test uses the {@link PdfGraphicsRemoverByColor} to remove
     * blue underlines from the given document. This content editor
     * actually drops any path stroking or filling of a given color.
     * </p>
     * @throws IOException
     */
    @Test
    public void testRemoveBlueLinesFromEDQMReportsIssuesOfNonComplianceWithToothMac() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("EDQM_reports_issues_of_non-compliance_with_tooth__Mac.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "EDQM_reports_issues_of_non-compliance_with_tooth__Mac-RemoveBlueLines.pdf"));
                PdfWriter pdfWriter = new PdfWriter(result);
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter) )
        {
            PdfCanvasEditor editor = new PdfGraphicsRemoverByColor(ColorConstants.BLUE);
            for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++)
            {
                editor.editPage(pdfDocument, i);
            }
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/58029533/traverse-whole-pdf-and-remove-underlines-of-hyperlinks-annotations-only-itex">
     * Traverse whole PDF and Remove underlines of hyperlinks (annotations) only + iText
     * </a>
     * <br/>
     * <a href="https://raad-dev-test.s3.ap-south-1.amazonaws.com/36/2019-08-30/originalFile.pdf">
     * originalFile.pdf
     * </a>
     * <p>
     * This test uses the {@link PdfGraphicsRemoverByColor} to remove
     * blue underlines from the given document. This content editor
     * actually drops any path stroking or filling of a given color.
     * </p>
     * @throws IOException
     */
    @Test
    public void testRemoveBlueLinesFromOriginalFile() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("originalFile.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "originalFile-RemoveBlueLines.pdf"));
                PdfWriter pdfWriter = new PdfWriter(result);
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter) )
        {
            PdfCanvasEditor editor = new PdfGraphicsRemoverByColor(ColorConstants.BLUE);
            for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++)
            {
                editor.editPage(pdfDocument, i);
            }
        }
    }

    /**
     * This content editor drops any path stroking or filling of a given color.
     *
     * @see RemoveGraphicsByColor#testRemoveBlueLinesFromControlOfNitrosamineImpuritiesInSartansRev()
     * @see RemoveGraphicsByColor#testRemoveBlueLinesFromEDQMReportsIssuesOfNonComplianceWithToothMac()
     * @see RemoveGraphicsByColor#testRemoveBlueLinesFromOriginalFile()
     */
    class PdfGraphicsRemoverByColor extends PdfCanvasEditor {
        public PdfGraphicsRemoverByColor(Color color) {
            this.color = color;
        }

        @Override
        protected void write(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands)
        {
            String operatorString = operator.toString();

            if (color.equals(getGraphicsState().getFillColor())) {
                switch (operatorString) {
                case "f":
                case "f*":
                case "F":
                    operatorString = "n";
                    break;
                case "b":
                case "b*":
                    operatorString = "s";
                    break;
                case "B":
                case "B*":
                    operatorString = "S";
                    break;
                }
            }

            if (color.equals(getGraphicsState().getStrokeColor())) {
                switch (operatorString) {
                case "s":
                case "S":
                    operatorString = "n";
                    break;
                case "b":
                case "B":
                    operatorString = "f";
                    break;
                case "b*":
                case "B*":
                    operatorString = "f*";
                    break;
                }
            }

            operator = new PdfLiteral(operatorString);
            operands.set(operands.size() - 1, operator);
            super.write(processor, operator, operands);
        }

        final Color color;
    }
}
