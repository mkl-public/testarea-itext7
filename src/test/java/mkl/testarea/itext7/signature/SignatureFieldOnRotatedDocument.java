package mkl.testarea.itext7.signature;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.ExternalBlankSignatureContainer;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSignatureAppearance.RenderingMode;
import com.itextpdf.signatures.PdfSigner;

/**
 * @author mkl
 */
public class SignatureFieldOnRotatedDocument {
    final static File RESULT_FOLDER = new File("target/test-outputs", "signature");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
    }

    /**
     * <a href="https://stackoverflow.com/questions/60373757/add-signature-field-on-rotated-document">
     * Add signature field on rotated document
     * </a>
     * <br/>
     * <a href="https://1drv.ms/b/s!AvIgyv7xAxxoihVIJBafez7kcmQ3?e=u2GBOy">
     * Signature Appearance issue on 90 deg rotated doc.pdf
     * </a> (reduced to its last unsigned revision)
     * <p>
     * This test uses iText to generate the signature appearance. The result
     * is proper. But the OP apparently wants a different arrangement.
     * </p>
     */
    @Test
    public void testSignMuddassirAwan() throws IOException, GeneralSecurityException {
        File destPath = new File(RESULT_FOLDER, "Signature Appearance issue on 90 deg rotated doc-SignedAppByIText.pdf");
        try (   InputStream resource = getClass().getResourceAsStream("Signature Appearance issue on 90 deg rotated doc.pdf");
                InputStream imageResource = getClass().getResourceAsStream("MuddassirAwan.png") ) {
            ImageData imageData = ImageDataFactory.create(StreamUtil.inputStreamToArray(imageResource));

            PdfReader pdfReader = new PdfReader(resource);
            PdfSigner pdfSigner = new PdfSigner(pdfReader, new FileOutputStream(destPath), new StampingProperties().useAppendMode());

            pdfSigner.setFieldName("SH_SIGNATURE_417868");

            PdfSignatureAppearance sigAppearance = pdfSigner.getSignatureAppearance();
            sigAppearance.setContact("ContactInfo");
            sigAppearance.setLocation("Location");
            sigAppearance.setReason("SigningReason");
            sigAppearance.setLayer2Text("Muddassir Awan");
            sigAppearance.setSignatureGraphic(imageData);
            sigAppearance.setRenderingMode(RenderingMode.GRAPHIC_AND_DESCRIPTION);
            sigAppearance.setSignatureCreator("Muddassir Awan");

            int estimatedSize = 12000;
            pdfSigner.signExternalContainer(new ExternalBlankSignatureContainer(PdfName.Adobe_PPKLite, PdfName.Adbe_pkcs7_detached), estimatedSize);
        }
    }

}
