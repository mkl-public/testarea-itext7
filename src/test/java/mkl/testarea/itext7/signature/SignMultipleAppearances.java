package mkl.testarea.itext7.signature;

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
import com.itextpdf.forms.PdfSigFieldLock;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfSignatureFormField;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PdfSigner.CryptoStandard;
import com.itextpdf.signatures.PrivateKeySignature;

/**
 * @author mkl
 */
public class SignMultipleAppearances {
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
     * <p>
     * This test illustrates how to easily create signatures with
     * multiple appearances in a way that doesn't conflict directly
     * with the specification, i.e. by using multiple signature
     * fields sharing the same signature dictionary with a single
     * appearance each.
     * </p>
     * <p>
     * This approach re-uses the original code of the protected method
     * {@link PdfSigner#createNewSignatureFormField(PdfAcroForm, String)}
     * to create a common base field and later on removes undesired
     * parts thereof. See {@link #testMultipleAppearancesMkII()} for an
     * approach building all structure itself.
     * </p>
     */
    @Test
    public void testMultipleAppearancesMkI() throws IOException, GeneralSecurityException {
        try (
            InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf");
            PdfReader pdfReader = new PdfReader(resource);
            OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "test-MultipleAppearancesMkI.pdf"))
        ) {
            PdfSigner pdfSigner = new PdfSigner(pdfReader, result, new StampingProperties()) {
                @Override
                protected PdfSigFieldLock createNewSignatureFormField(PdfAcroForm acroForm, String name)
                        throws IOException {
                    PdfSigFieldLock pdfSigFieldLock = super.createNewSignatureFormField(acroForm, name);

                    PdfSignatureFormField sigField = (PdfSignatureFormField) acroForm.getField(fieldName);

                    PdfWidgetAnnotation widget = sigField.getWidgets().get(0);
                    widget.getPage().removeAnnotation(widget);
                    sigField.remove(PdfName.AP);
                    sigField.remove(PdfName.Subtype);
                    sigField.remove(PdfName.P);
                    sigField.remove(PdfName.FT);

                    PdfPage pdfPage = document.getFirstPage();
                    PdfDictionary signatureDictionary = cryptoDictionary.getPdfObject();
                    sigField.addKid(createSubField(pdfPage, 10, 10, 100, 20, "bottom left", signatureDictionary, document));
                    sigField.addKid(createSubField(pdfPage, 400, 10, 100, 20, "bottom right", signatureDictionary, document));
                    sigField.addKid(createSubField(pdfPage, 10, 600, 100, 20, "top left", signatureDictionary, document));
                    sigField.addKid(createSubField(pdfPage, 400, 600, 100, 20, "top right", signatureDictionary, document));

                    return pdfSigFieldLock;
                }
            };
            
            IExternalSignature pks = new PrivateKeySignature(pk, "SHA256", BouncyCastleProvider.PROVIDER_NAME);
            pdfSigner.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, CryptoStandard.CMS);
        }
    }

    /**
     * <p>
     * This test illustrates how to easily create signatures with
     * multiple appearances in a way that doesn't conflict directly
     * with the specification, i.e. by using multiple signature
     * fields sharing the same signature dictionary with a single
     * appearance each.
     * </p>
     * <p>
     * This approach builds all the form field structure itself. See
     * {@link #testMultipleAppearancesMkI()} for an approach that
     * re-uses the original code of the protected method
     * {@link PdfSigner#createNewSignatureFormField(PdfAcroForm, String)}
     * to create a common base field and later on removes undesired
     * parts thereof.
     * </p>
     */
    @Test
    public void testMultipleAppearancesMkII() throws IOException, GeneralSecurityException {
        try (
            InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf");
            PdfReader pdfReader = new PdfReader(resource);
            OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "test-MultipleAppearancesMkII.pdf"))
        ) {
            PdfSigner pdfSigner = new PdfSigner(pdfReader, result, new StampingProperties()) {
                @Override
                protected PdfSigFieldLock createNewSignatureFormField(PdfAcroForm acroForm, String name)
                        throws IOException {
                    PdfSignatureFormField sigField = PdfFormField.createSignature(document);
                    sigField.setFieldName(name);

                    acroForm.addField(sigField, null);

                    if (acroForm.getPdfObject().isIndirect()) {
                        acroForm.setModified();
                    } else {
                        document.getCatalog().setModified();
                    }

                    sigField.remove(PdfName.FT);

                    PdfPage pdfPage = document.getFirstPage();
                    PdfDictionary signatureDictionary = cryptoDictionary.getPdfObject();
                    sigField.addKid(createSubField(pdfPage, 10, 10, 100, 20, "bottom left", signatureDictionary, document));
                    sigField.addKid(createSubField(pdfPage, 400, 10, 100, 20, "bottom right", signatureDictionary, document));
                    sigField.addKid(createSubField(pdfPage, 10, 600, 100, 20, "top left", signatureDictionary, document));
                    sigField.addKid(createSubField(pdfPage, 400, 600, 100, 20, "top right", signatureDictionary, document));

                    return sigField.getSigFieldLockDictionary();
                }
            };
            
            IExternalSignature pks = new PrivateKeySignature(pk, "SHA256", BouncyCastleProvider.PROVIDER_NAME);
            pdfSigner.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, CryptoStandard.CMS);
        }
    }

    /**
     * @see #testMultipleAppearancesMkI()
     * @see #testMultipleAppearancesMkII()
     */
    PdfFormField createSubField(PdfPage page, float x, float y, float width, float height, String name, PdfDictionary signatureDictionary, PdfDocument pdfDocument) {
        PdfFormXObject appearance = new PdfFormXObject(new Rectangle(width, height));
        PdfCanvas pdfCanvas = new PdfCanvas(appearance, pdfDocument);
        try (   Canvas canvas = new Canvas(pdfCanvas, new Rectangle(width, height))) {
            canvas.showTextAligned(name, 2, 2, TextAlignment.LEFT);
        }

        PdfDictionary ap = new PdfDictionary();
        ap.put(PdfName.N, appearance.getPdfObject());

        PdfWidgetAnnotation widget = new PdfWidgetAnnotation(new Rectangle(x, y, width, height));
        widget.setFlags(PdfAnnotation.PRINT | PdfAnnotation.LOCKED);
        widget.setPage(page);
        widget.put(PdfName.AP, ap);

        PdfSignatureFormField sigField = PdfFormField.createSignature(pdfDocument);
        sigField.setFieldName(name);
        sigField.addKid(widget);
        sigField.put(PdfName.V, signatureDictionary);

        page.addAnnotation(widget);

        return sigField;
    }
}
