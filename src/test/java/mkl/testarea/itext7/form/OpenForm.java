package mkl.testarea.itext7.form;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;

/**
 * @author mkl
 */
public class OpenForm {
    final static File RESULT_FOLDER = new File("target/test-outputs", "form");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/75825481/itext-pdfacroform-seems-slow">
     * iText - PdfAcroForm seems slow
     * </a>
     * <br/>
     * <a href="https://acrobat.adobe.com/link/track?uri=urn:aaid:scds:US:3d5185a1-f433-3773-9a8c-2893c9db9a97">
     * HYUNDAI_CA_MOTOR_VEHICLE_LEASE_AGREEMENT_IMPACT_fc029ce2-4347-42b0-8c42-f597c0e21bee.pdf
     * </a>
     * <p>
     * On a laptop from 2020 opening the form takes 90..110 ms which seems acceptable.
     * Also the result file does not contain any form changes.
     * </p>
     */
    @Test
    public void testOpenInAppendModeHyundaiCaMotorVehicleLeaseAgreementImpact() throws IOException {
        try (
            InputStream resource = getClass().getResourceAsStream("HYUNDAI_CA_MOTOR_VEHICLE_LEASE_AGREEMENT_IMPACT_fc029ce2-4347-42b0-8c42-f597c0e21bee.pdf");
            PdfReader pdfReader = new PdfReader(resource);
            PdfWriter pdfWriter = new PdfWriter(new File(RESULT_FOLDER, "HYUNDAI_CA_MOTOR_VEHICLE_LEASE_AGREEMENT_IMPACT_fc029ce2-4347-42b0-8c42-f597c0e21bee-appended.pdf"));
            PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter, new StampingProperties().useAppendMode())
        ) {
            long time = System.currentTimeMillis();
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDocument, true);
            long timeEnd = System.currentTimeMillis();
            System.out.printf("Opening form took %sms.\n", timeEnd-time);
        }
    }

}
