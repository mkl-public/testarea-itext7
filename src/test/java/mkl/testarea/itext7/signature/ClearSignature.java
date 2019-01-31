package mkl.testarea.itext7.signature;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.List;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.SignatureUtil;
import com.itextpdf.signatures.PdfSigner.CryptoStandard;

/**
 * @author mkl
 */
public class ClearSignature {
    final static File RESULT_FOLDER = new File("target/test-outputs", "signature");

    final static String path = "keystores/demo-rsa2048.p12";
    final static char[] pass = "demo-rsa2048".toCharArray();
    static PrivateKey pk;
    static Certificate[] chain;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);

        KeyStore ks = KeyStore.getInstance("pkcs12", "SunJSSE");
        ks.load(new FileInputStream(path), pass);
        String alias = "";
        Enumeration<String> aliases = ks.aliases();
        while (alias.equals("demo") == false && aliases.hasMoreElements()) {
            alias = aliases.nextElement();
        }
        pk = (PrivateKey) ks.getKey(alias, pass);
        chain = ks.getCertificateChain(alias);
    }

    /**
     * <a href="https://stackoverflow.com/questions/54177092/how-to-remove-seal-when-i-sign-many-times">
     * how to remove seal When I sign many times
     * </a>
     * <br/>
     * step6_signed_by_alice_bob_carol_and_dave.pdf
     * <br/>
     * (result of iText signature whitepaper example C2_11_SignatureWorkflow)
     * <p>
     * This test shows how to clear the outermost signed signature
     * field in an incremental update. Beware: The former signature
     * remains in the revision history of the result PDF.
     * </p>
     */
    @Test
    public void testClearLastSignatureIncrementally() throws IOException, GeneralSecurityException {
        String lastSignatureName = null;

        File clearedFile = new File(RESULT_FOLDER, "step6_signed_by_alice_bob_carol_and_dave-cleared-incrementally.pdf");
        try (   InputStream source = getClass().getResourceAsStream("step6_signed_by_alice_bob_carol_and_dave.pdf");
                PdfReader pdfReader = new PdfReader(source);
                PdfWriter pdfWriter = new PdfWriter(clearedFile);
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter, new StampingProperties().useAppendMode())) {
            lastSignatureName = clearLastSignature(pdfDocument);
        }

        File resignedFile = new File(RESULT_FOLDER, "step6_signed_by_alice_bob_carol_and_dave-cleared-incrementally-resigned.pdf");
        try (   PdfReader pdfReader = new PdfReader(clearedFile);
                FileOutputStream result = new FileOutputStream(resignedFile)    ) {
            PdfSigner signer = new PdfSigner(pdfReader, result, new StampingProperties().useAppendMode());
            resign(signer, lastSignatureName);
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/54177092/how-to-remove-seal-when-i-sign-many-times">
     * how to remove seal When I sign many times
     * </a>
     * <br/>
     * step_4_signed_by_alice_bob_carol_and_dave.pdf
     * <br/>
     * (result of iText signature whitepaper example C2_12_LockFields)
     * <p>
     * This test shows how to clear the outermost signed signature
     * field in an incremental update. This appears to be accepted
     * by Adobe Reader even in locked PDFs. Beware: The former
     * signature remains in the revision history of the result PDF.
     * </p>
     */
    @Test
    public void testClearLockingLastSignatureIncrementally() throws IOException, GeneralSecurityException {
        String lastSignatureName = null;

        File clearedFile = new File(RESULT_FOLDER, "step_4_signed_by_alice_bob_carol_and_dave-cleared-incrementally.pdf");
        try (   InputStream source = getClass().getResourceAsStream("step_4_signed_by_alice_bob_carol_and_dave.pdf");
                PdfReader pdfReader = new PdfReader(source);
                PdfWriter pdfWriter = new PdfWriter(clearedFile);
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter, new StampingProperties().useAppendMode())) {
            lastSignatureName = clearLastSignature(pdfDocument);
        }

        File resignedFile = new File(RESULT_FOLDER, "step_4_signed_by_alice_bob_carol_and_dave-cleared-incrementally-resigned.pdf");
        try (   PdfReader pdfReader = new PdfReader(clearedFile);
                FileOutputStream result = new FileOutputStream(resignedFile)    ) {
            PdfSigner signer = new PdfSigner(pdfReader, result, new StampingProperties().useAppendMode());
            resign(signer, lastSignatureName);
        }
    }

    String clearLastSignature(PdfDocument pdfDocument) {
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);
        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(pdfDocument, false);

        List<String> signatureNames = signatureUtil.getSignatureNames();
        if (signatureNames != null && signatureNames.size() > 0) {
            String lastSignatureName = signatureNames.get(signatureNames.size() - 1);
            PdfFormField lastSignatureField = acroForm.getField(lastSignatureName);
            if (null != lastSignatureField.getPdfObject().remove(PdfName.V))
                lastSignatureField.getPdfObject().setModified();
            for (PdfWidgetAnnotation pdfWidgetAnnotation : lastSignatureField.getWidgets()) {
                if (pdfWidgetAnnotation.getPdfObject().remove(PdfName.AP) != null)
                    pdfWidgetAnnotation.getPdfObject().setModified();
            }
            return lastSignatureName;
        }
        return null;
    }

    void resign(PdfSigner signer, String lastSignatureName) throws IOException, GeneralSecurityException {
        signer.setFieldName(lastSignatureName);
        IExternalSignature pks = new PrivateKeySignature(pk, "SHA512", BouncyCastleProvider.PROVIDER_NAME);
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, CryptoStandard.CADES);
    }
}
