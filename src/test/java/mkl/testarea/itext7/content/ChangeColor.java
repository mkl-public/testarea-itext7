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
public class ChangeColor {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/57993761/traverse-whole-pdf-and-change-blue-color-to-black-change-color-of-underlines-a">
     * Traverse whole PDF and change blue color to black ( Change color of underlines as well) + iText
     * </a>
     * <br/>
     * <a href="https://raad-dev-test.s3.ap-south-1.amazonaws.com/36/2019-08-30/originalFile.pdf">
     * originalFile.pdf
     * </a>
     * <p>
     * This canvas editor is related to the editor in
     * {@link EditPageContent#testChangeBlackTextToGreenDocument()}.
     * In contrast to that editor, though, the editor here shall
     * change the fill color of arbitrary content, not only text
     * content. Thus, this actually is simpler than the original.
     * </p>
     */
    @Test
    public void testChangeFillRgbBlueToBlack() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("originalFile.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "originalFile-FillRgbBlueToBlack.pdf"));
                PdfWriter pdfWriter = new PdfWriter(result);
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter) )
        {
            PdfCanvasEditor editor = new PdfCanvasEditor()
            {
                @Override
                protected void write(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands)
                {
                    String operatorString = operator.toString();

                    if (SET_FILL_RGB.equals(operatorString) && operands.size() == 4) {
                        if (isApproximatelyEqual(operands.get(0), 0) &&
                                isApproximatelyEqual(operands.get(1), 0) &&
                                isApproximatelyEqual(operands.get(2), 1)) {
                            super.write(processor, new PdfLiteral("g"), Arrays.asList(new PdfNumber(0), new PdfLiteral("g")));
                            return;
                        }
                    }
                    
                    super.write(processor, operator, operands);
                }

                boolean isApproximatelyEqual(PdfObject number, float reference) {
                    return number instanceof PdfNumber && Math.abs(reference - ((PdfNumber)number).floatValue()) < 0.01f;
                }

                final String SET_FILL_RGB = "rg";
            };
            for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++)
            {
                editor.editPage(pdfDocument, i);
            }
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/57993761/traverse-whole-pdf-and-change-blue-color-to-black-change-color-of-underlines-a">
     * Traverse whole PDF and change blue color to black ( Change color of underlines as well) + iText
     * </a>
     * <br/>
     * <a href="https://raad-dev-test.s3.ap-south-1.amazonaws.com/36/2019-08-30/Control_of_nitrosamine_impurities_in_sartans__rev.pdf">
     * Control_of_nitrosamine_impurities_in_sartans__rev.pdf
     * </a>
     * <p>
     * This canvas editor extends the editor in
     * {@link #testChangeFillRgbBlueToBlack()}
     * by not only replacing fill colors but
     * also stroke colors.
     * </p>
     */
    @Test
    public void testChangeRgbBlueToBlackControlOfNitrosamineImpuritiesInSartansRev() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("Control_of_nitrosamine_impurities_in_sartans__rev.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "Control_of_nitrosamine_impurities_in_sartans__rev-FillRgbBlueToBlack.pdf"));
                PdfWriter pdfWriter = new PdfWriter(result);
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter) )
        {
            PdfCanvasEditor editor = new PdfCanvasEditor()
            {
                @Override
                protected void write(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands)
                {
                    String operatorString = operator.toString();

                    if (SET_FILL_RGB.equals(operatorString) && operands.size() == 4) {
                        if (isApproximatelyEqual(operands.get(0), 0) &&
                                isApproximatelyEqual(operands.get(1), 0) &&
                                isApproximatelyEqual(operands.get(2), 1)) {
                            super.write(processor, new PdfLiteral("g"), Arrays.asList(new PdfNumber(0), new PdfLiteral("g")));
                            return;
                        }
                    }

                    if (SET_STROKE_RGB.equals(operatorString) && operands.size() == 4) {
                        if (isApproximatelyEqual(operands.get(0), 0) &&
                                isApproximatelyEqual(operands.get(1), 0) &&
                                isApproximatelyEqual(operands.get(2), 1)) {
                            super.write(processor, new PdfLiteral("G"), Arrays.asList(new PdfNumber(0), new PdfLiteral("G")));
                            return;
                        }
                    }

                    super.write(processor, operator, operands);
                }

                boolean isApproximatelyEqual(PdfObject number, float reference) {
                    return number instanceof PdfNumber && Math.abs(reference - ((PdfNumber)number).floatValue()) < 0.01f;
                }

                final String SET_FILL_RGB = "rg";
                final String SET_STROKE_RGB = "RG";
            };
            for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++)
            {
                editor.editPage(pdfDocument, i);
            }
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/57993761/traverse-whole-pdf-and-change-blue-color-to-black-change-color-of-underlines-a">
     * Traverse whole PDF and change blue color to black ( Change color of underlines as well) + iText
     * </a>
     * <br/>
     * <a href="https://raad-dev-test.s3.ap-south-1.amazonaws.com/36/2019-08-30/EDQM_reports_issues_of_non-compliance_with_tooth__Mac.pdf">
     * EDQM_reports_issues_of_non-compliance_with_tooth__Mac.pdf
     * </a>
     * <p>
     * This canvas editor is the same as in
     * {@link #testChangeRgbBlueToBlackControlOfNitrosamineImpuritiesInSartansRev()}.
     * </p>
     */
    @Test
    public void testChangeRgbBlueToBlackEdqmReportsIssuesOfNonComplianceWithToothMac() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("EDQM_reports_issues_of_non-compliance_with_tooth__Mac.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "EDQM_reports_issues_of_non-compliance_with_tooth__Mac-FillRgbBlueToBlack.pdf"));
                PdfWriter pdfWriter = new PdfWriter(result);
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter) )
        {
            PdfCanvasEditor editor = new PdfCanvasEditor()
            {
                @Override
                protected void write(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands)
                {
                    String operatorString = operator.toString();

                    if (SET_FILL_RGB.equals(operatorString) && operands.size() == 4) {
                        if (isApproximatelyEqual(operands.get(0), 0) &&
                                isApproximatelyEqual(operands.get(1), 0) &&
                                isApproximatelyEqual(operands.get(2), 1)) {
                            super.write(processor, new PdfLiteral("g"), Arrays.asList(new PdfNumber(0), new PdfLiteral("g")));
                            return;
                        }
                    }

                    if (SET_STROKE_RGB.equals(operatorString) && operands.size() == 4) {
                        if (isApproximatelyEqual(operands.get(0), 0) &&
                                isApproximatelyEqual(operands.get(1), 0) &&
                                isApproximatelyEqual(operands.get(2), 1)) {
                            super.write(processor, new PdfLiteral("G"), Arrays.asList(new PdfNumber(0), new PdfLiteral("G")));
                            return;
                        }
                    }

                    super.write(processor, operator, operands);
                }

                boolean isApproximatelyEqual(PdfObject number, float reference) {
                    return number instanceof PdfNumber && Math.abs(reference - ((PdfNumber)number).floatValue()) < 0.01f;
                }

                final String SET_FILL_RGB = "rg";
                final String SET_STROKE_RGB = "RG";
            };
            for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++)
            {
                editor.editPage(pdfDocument, i);
            }
        }
    }
}
