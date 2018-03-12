/**
 * 
 */
package mkl.testarea.itext7.signature;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Enumeration;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PdfSigner.CryptoStandard;
import com.itextpdf.signatures.PrivateKeySignature;

/**
 * @author mkl
 */
public class CreateDSS1376TestCases {
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
        while (alias.equals("demo") == false && aliases.hasMoreElements())
        {
            alias = aliases.nextElement();
        }
        pk = (PrivateKey) ks.getKey(alias, pass);
        chain = ks.getCertificateChain(alias);
    }

    /**
     * <a href="https://ec.europa.eu/cefdigital/tracker/browse/DSS-1376">
     * DSS-1376 - PAdES - difference between the validator.getOriginal and the original doc
     * </a>
     * <p>
     * This test creates a test file in which the original DSS assumption
     * that each signature is applied in an incremental update and each
     * incremental update includes a signature, are not fulfilled: The
     * first document revision here already contains a signature and
     * the first few incremental updates don't.
     * </p>
     */
    @Test
    public void testCreateDSS1376UpdatesForFillins() throws IOException, GeneralSecurityException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (   PdfWriter pdfWriter = new PdfWriter(baos);
                PdfDocument pdfDocument = new PdfDocument(pdfWriter)) {
            new Document(pdfDocument).add(new Paragraph("Test with fields and incremental updates for field fill-ins."));
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDocument, true);
            addTextField(pdfDocument, form, "Field1", 600);
            addTextField(pdfDocument, form, "Field2", 500);
            addTextField(pdfDocument, form, "Field3", 400);
            addTextField(pdfDocument, form, "Field4", 300);
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        baos.reset();
        sign(bais, baos, "Sig1", CryptoStandard.CADES, PdfSigner.CERTIFIED_FORM_FILLING, false, 600);

        bais = new ByteArrayInputStream(baos.toByteArray());
        baos.reset();
        fillTextField(bais, baos, "Field1", "Value One");

        bais = new ByteArrayInputStream(baos.toByteArray());
        baos.reset();
        fillTextField(bais, baos, "Field2", "Value Two");

        bais = new ByteArrayInputStream(baos.toByteArray());
        baos.reset();
        fillTextField(bais, baos, "Field3", "Value Three");

        bais = new ByteArrayInputStream(baos.toByteArray());
        baos.reset();
        fillTextField(bais, baos, "Field4", "Value Four");

        bais = new ByteArrayInputStream(baos.toByteArray());
        baos.reset();
        sign(bais, new FileOutputStream(new File(RESULT_FOLDER, "DSS1376-updates-for-fillins.pdf")), "Sig2", CryptoStandard.CADES, PdfSigner.NOT_CERTIFIED, true, 500);
    }

    public void addTextField(PdfDocument pdfDocument, PdfAcroForm form, String fieldName, int y) {
        Rectangle rect = new Rectangle(30, y, 100, 20);
        PdfTextFormField field = PdfFormField.createText(pdfDocument, rect, fieldName);
        form.addField(field, pdfDocument.getFirstPage());
    }

    public void fillTextField(InputStream original, OutputStream result, String fieldName, String value) throws IOException {
        try (   PdfReader pdfReader = new PdfReader(original);
                PdfWriter pdfWriter = new PdfWriter(result);
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter, new StampingProperties().useAppendMode())) {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDocument, true);
            form.getField(fieldName).setValue(value);
        }
    }

    public void sign(InputStream original, OutputStream result, String name, CryptoStandard subfilter,
            int certificationLevel, boolean isAppendMode, int y) throws IOException, GeneralSecurityException {
        String reason = "Just another illusionary reason";
        String location = "Right around the corner";
        boolean setReuseAppearance = false;
        Rectangle rectangleForNewField = new Rectangle(200, y, 200, 50);
        String digestAlgorithm = "SHA512";

        PdfReader reader = new PdfReader(original);
        PdfSigner signer = new PdfSigner(reader, result, isAppendMode);

        signer.setCertificationLevel(certificationLevel);

        // Creating the appearance
        PdfSignatureAppearance appearance = signer.getSignatureAppearance()
                .setReason(reason)
                .setLocation(location)
                .setReuseAppearance(setReuseAppearance);

        if (rectangleForNewField != null) {
            appearance.setPageRect(rectangleForNewField);
        }

        signer.setFieldName(name);

        // Creating the signature
        IExternalSignature pks = new PrivateKeySignature(pk, digestAlgorithm, BouncyCastleProvider.PROVIDER_NAME);
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, subfilter);
    }

}
