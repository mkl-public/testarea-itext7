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
import java.util.Collection;
import java.util.Enumeration;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.IOcspClient;
import com.itextpdf.signatures.ITSAClient;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSignatureAppearance.RenderingMode;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.ProviderDigest;

/**
 * @author mkl
 */
public class AddPageAndSign
{
    final static File RESULT_FOLDER = new File("target/test-outputs", "signature");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
    }

    /**
     * <a href="http://stackoverflow.com/questions/39296225/adding-a-new-page-to-pdf-and-create-signature-with-itext-7">
     * Adding a new page to PDF and create signature with iText 7
     * </a>
     * <p>
     * The original code used "SunJSSE" as provider argument to the <code>sign</code> call.
     * Using that setting the signing procedure here fails with a
     * <em>java.security.NoSuchAlgorithmException: no such algorithm: SHA1 for provider SunJSSE</em>.
     * Changing the provider argument to "BC" creates a properly certified PDF.
     * </p>
     */
    @Test
    public void testSignLikeXinDHA() throws GeneralSecurityException, IOException, XMPException
    {
        String path = "keystores/demo-rsa2048.p12";
        char[] pass = "demo-rsa2048".toCharArray();

        KeyStore ks = KeyStore.getInstance("pkcs12", "SunJSSE");
        ks.load(new FileInputStream(path), pass);
        String alias = "";
        Enumeration<String> aliases = ks.aliases();
        while (alias.equals("demo") == false && aliases.hasMoreElements())
        {
            alias = aliases.nextElement();
        }
        PrivateKey pk = (PrivateKey) ks.getKey(alias, pass);
        Certificate[] chain = ks.getCertificateChain(alias);

        try ( InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf"))
        {
            sign(resource, new File(RESULT_FOLDER, "test_XinDHA_signed_initial.pdf").getAbsolutePath(),
                    chain, pk, DigestAlgorithms.SHA1, /*"SunJSSE"*/"BC", PdfSigner.CryptoStandard.CMS, "Test", "Test",
                    null, null, null, 0, true);
        }
    }

    /**
     * <a href="http://stackoverflow.com/questions/39296225/adding-a-new-page-to-pdf-and-create-signature-with-itext-7">
     * Adding a new page to PDF and create signature with iText 7
     * </a>
     * <p>
     * The signing method of the OP.
     * </p>
     */
    public void sign(InputStream src, String dest, Certificate[] chain, PrivateKey pk, String digestAlgorithm,
            String provider, PdfSigner.CryptoStandard subfilter, String reason, String location,
            Collection<ICrlClient> crlList, IOcspClient ocspClient, ITSAClient tsaClient, int estimatedSize,
            boolean initial)
            throws GeneralSecurityException, IOException, XMPException
    {
        // Creating the reader and the signer

        PdfDocument document = new PdfDocument(new PdfReader(src), new PdfWriter(dest + "_temp"));
        if (initial == true)
        {
            document.addNewPage();
        }
        int pageCount = document.getNumberOfPages();
        document.close();
        PdfSigner signer = new PdfSigner(new PdfReader(dest + "_temp"), new FileOutputStream(dest), true);
        // Creating the appearance
        if (initial == true)
        {
            signer.setCertificationLevel(PdfSigner.CERTIFIED_FORM_FILLING_AND_ANNOTATIONS);
        }
        PdfSignatureAppearance appearance = signer.getSignatureAppearance().setReason(reason).setLocation(location)
                .setReuseAppearance(false);
        Rectangle rect = new Rectangle(10, 400, 100, 100);
        appearance.setPageRect(rect).setPageNumber(pageCount);
        appearance.setRenderingMode(RenderingMode.NAME_AND_DESCRIPTION);
        signer.setFieldName(signer.getNewSigFieldName());
        // Creating the signature
        IExternalSignature pks = new PrivateKeySignature(pk, digestAlgorithm, provider);
        ProviderDigest digest = new ProviderDigest(provider);
        signer.signDetached(digest, pks, chain, crlList, ocspClient, tsaClient, estimatedSize, subfilter);
    }
}
