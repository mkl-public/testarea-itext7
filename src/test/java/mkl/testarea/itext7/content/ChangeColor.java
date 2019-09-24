package mkl.testarea.itext7.content;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    /**
     * <a href="https://stackoverflow.com/questions/57993761/traverse-whole-pdf-and-change-blue-color-to-black-change-color-of-underlines-a">
     * Traverse whole PDF and change blue color to black ( Change color of underlines as well) + iText
     * </a>
     * <br/>
     * <a href="https://raad-dev-test.s3.ap-south-1.amazonaws.com/36/2019-08-30/originalFile.pdf">
     * originalFile.pdf
     * </a>
     * <p>
     * This test applies the {@link AllRgbBlueToBlackConverter} to the
     * "originalFile.pdf" test document; check in particular page 14.
     * </p>
     */
    @Test
    public void testAllRgbBlueToBlackConverterOriginalFile() throws IOException {
        testAllRgbBlueToBlackConverter("originalFile.pdf", "originalFile-AllRgbBlueToBlack.pdf");
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
     * This test applies the {@link AllRgbBlueToBlackConverter} to the
     * "Control_of_nitrosamine_impurities_in_sartans__rev.pdf" test
     * document.
     * </p>
     */
    @Test
    public void testAllRgbBlueToBlackConverterControlOfNitrosamineImpuritiesInSartansRev() throws IOException {
        testAllRgbBlueToBlackConverter("Control_of_nitrosamine_impurities_in_sartans__rev.pdf", "Control_of_nitrosamine_impurities_in_sartans__rev-AllRgbBlueToBlack.pdf");
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
     * This test applies the {@link AllRgbBlueToBlackConverter} to the
     * "EDQM_reports_issues_of_non-compliance_with_tooth__Mac.pdf" test
     * document.
     * </p>
     */
    @Test
    public void testAllRgbBlueToBlackConverterEdqmReportsIssuesOfNonComplianceWithToothMac() throws IOException {
        testAllRgbBlueToBlackConverter("EDQM_reports_issues_of_non-compliance_with_tooth__Mac.pdf", "EDQM_reports_issues_of_non-compliance_with_tooth__Mac-AllRgbBlueToBlack.pdf");
    }

    /**
     * <a href="https://stackoverflow.com/questions/57993761/traverse-whole-pdf-and-change-blue-color-to-black-change-color-of-underlines-a">
     * Traverse whole PDF and change blue color to black ( Change color of underlines as well) + iText
     * </a>
     * <br/>
     * <a href="https://raad-dev-test.s3.ap-south-1.amazonaws.com/36/2019-08-30/021549Orig1s025_aprepitant_clinpharm_prea_Mac.pdf">
     * 021549Orig1s025_aprepitant_clinpharm_prea_Mac.pdf
     * </a>
     * <p>
     * This test applies the {@link AllRgbBlueToBlackConverter} to the
     * "021549Orig1s025_aprepitant_clinpharm_prea_Mac.pdf" test
     * document; check in particular page 41.
     * </p>
     */
    @Test
    public void testAllRgbBlueToBlackConverter021549Orig1s025AprepitantClinpharmPreaMac() throws IOException {
        testAllRgbBlueToBlackConverter("021549Orig1s025_aprepitant_clinpharm_prea_Mac.pdf", "021549Orig1s025_aprepitant_clinpharm_prea_Mac-AllRgbBlueToBlack.pdf");
    }

    /**
     * <a href="https://stackoverflow.com/questions/57993761/traverse-whole-pdf-and-change-blue-color-to-black-change-color-of-underlines-a">
     * Traverse whole PDF and change blue color to black ( Change color of underlines as well) + iText
     * </a>
     * <br/>
     * <a href="https://raad-dev-test.s3.ap-south-1.amazonaws.com/36/2019-08-30/400_206494S5_avibactam_and_ceftazidine_unireview_prea_Mac.pdf">
     * 400_206494S5_avibactam_and_ceftazidine_unireview_prea_Mac.pdf
     * </a>
     * <p>
     * This test applies the {@link AllRgbBlueToBlackConverter} to the
     * "400_206494S5_avibactam_and_ceftazidine_unireview_prea_Mac.pdf" test
     * document; check in particular page 60.
     * </p>
     */
    @Test
    public void testAllRgbBlueToBlackConverter400206494S5AvibactamAndCeftazidineUnireviewPreaMac() throws IOException {
        testAllRgbBlueToBlackConverter("400_206494S5_avibactam_and_ceftazidine_unireview_prea_Mac.pdf", "400_206494S5_avibactam_and_ceftazidine_unireview_prea_Mac-AllRgbBlueToBlack.pdf");
    }

    /**
     * <a href="https://stackoverflow.com/questions/57993761/traverse-whole-pdf-and-change-blue-color-to-black-change-color-of-underlines-a">
     * Traverse whole PDF and change blue color to black ( Change color of underlines as well) + iText
     * </a>
     * <p>
     * This test applies the {@link AllRgbBlueToBlackConverter} to the
     * the given argument resource.
     * </p>
     */
    void testAllRgbBlueToBlackConverter(String resourceName, String resultName) throws IOException {
        System.out.printf("\nConverting '%s'.\n", resourceName);
        try (   InputStream resource = getClass().getResourceAsStream(resourceName);
                PdfReader pdfReader = new PdfReader(resource);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, resultName));
                PdfWriter pdfWriter = new PdfWriter(result);
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter) ) {
            PdfCanvasEditor editor = new AllRgbBlueToBlackConverter();
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
     * <p>
     * This {@link PdfCanvasEditor} replaces a large range of blue'ish
     * RGB colors by black. Essentially all colors are considered blue
     * in which the blue component is the strongest by a at least 10%
     * and is at least .5 itself. This matches a wide spectrum from
     * purple to cyan light enough to be recognized as such.
     * </p>
     * <p>
     * All rg, RG, sc, SC, scn, and SCN instructions with three
     * parameters all of which have to be numeric are interpreted to
     * set RGB colors. This obviously is not correct in general but
     * appears to match in case of the OP's documents with DeviceRGB
     * and ICCBased RGB color spaces.
     * </p>
     */
    class AllRgbBlueToBlackConverter extends PdfCanvasEditor {
        @Override
        protected void write(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands)
        {
            String operatorString = operator.toString();

            if (RGB_SETTER_CANDIDATES.contains(operatorString) && operands.size() == 4) {
                if (isBlue(operands.get(0), operands.get(1), operands.get(2))) {
                    PdfNumber number0 = new PdfNumber(0);
                    operands.set(0, number0);
                    operands.set(1, number0);
                    operands.set(2, number0);
                }
            }

            super.write(processor, operator, operands);
        }

        boolean isBlue(PdfObject red, PdfObject green, PdfObject blue) {
            if (red instanceof PdfNumber && green instanceof PdfNumber && blue instanceof PdfNumber) {
                float r = ((PdfNumber)red).floatValue();
                float g = ((PdfNumber)green).floatValue();
                float b = ((PdfNumber)blue).floatValue();
                return b > .5f && r < .9f*b && g < .9f*b;
            }
            return false;
        }

        final Set<String> RGB_SETTER_CANDIDATES = new HashSet<>(Arrays.asList("rg", "RG", "sc", "SC", "scn", "SCN"));
    }
}
