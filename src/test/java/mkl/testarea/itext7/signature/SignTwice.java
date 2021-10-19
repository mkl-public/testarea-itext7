package mkl.testarea.itext7.signature;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Enumeration;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.io.Streams;
import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.ExternalBlankSignatureContainer;
import com.itextpdf.signatures.IExternalSignatureContainer;
import com.itextpdf.signatures.PdfSigner;

/**
 * @author mkl
 */
public class SignTwice {
    final static File RESULT_FOLDER = new File("target/test-outputs", "signature");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
    }

    /**
     * <a href="https://stackoverflow.com/questions/47217751/itext7-multiple-signature">
     * iText7 multiple signature
     * </a>
     * <br/>
     * <a href="https://www.dropbox.com/s/jitakxi83ywzolg/hello.pdf?dl=0">
     * hello.pdf
     * </a> as signed2-2.pdf
     * <p>
     * Indeed, Adobe Reader claims the first signature is broken.
     * </p>
     * @see #testSignTwiceLikeJDNewFirstRevision()
     * @see #testSignTwiceLikeJDNewFixedFile()
     */
    @Test
    public void testSignTwiceLikeJDNew() throws IOException {
        File intermediate = new File(RESULT_FOLDER, "signTwice-intermediate.pdf");
        File result = new File(RESULT_FOLDER, "signTwice-result.pdf");
        try ( InputStream resource = getClass().getResourceAsStream("signed2-2.pdf")) {
            SignMultPDF(Streams.readAll(resource), intermediate.getPath(), null, null, null);
            SignMultPDF(Files.readAllBytes(intermediate.toPath()), result.getPath(), null, null, null);
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/47217751/itext7-multiple-signature">
     * iText7 multiple signature
     * </a>
     * <br/>
     * starting revision of
     * <a href="https://www.dropbox.com/s/jitakxi83ywzolg/hello.pdf?dl=0">
     * hello.pdf
     * </a> as signed2-1.pdf
     * <p>
     * Adobe Reader does not show any issues if one takes the previous revision from
     * the original file.
     * </p>
     * @see #testSignTwiceLikeJDNew()
     * @see #testSignTwiceLikeJDNewFixedFile()
     */
    @Test
    public void testSignTwiceLikeJDNewFirstRevision() throws IOException {
        File intermediate = new File(RESULT_FOLDER, "signTwiceFirstRevision-intermediate.pdf");
        File result = new File(RESULT_FOLDER, "signTwiceFirstRevision-result.pdf");
        try ( InputStream resource = getClass().getResourceAsStream("signed2-1.pdf")) {
            SignMultPDF(Streams.readAll(resource), intermediate.getPath(), null, null, null);
            SignMultPDF(Files.readAllBytes(intermediate.toPath()), result.getPath(), null, null, null);
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/47217751/itext7-multiple-signature">
     * iText7 multiple signature
     * </a>
     * <br/>
     * fixed version of
     * <a href="https://www.dropbox.com/s/jitakxi83ywzolg/hello.pdf?dl=0">
     * hello.pdf
     * </a> as signed2-2-fixed.pdf
     * <p>
     * The OP's original file uses an empty indirect dictionary both as Info
     * and as Outlines dictionary. During signature creation iText fills this
     * Info dictionary and so changes the outlines. This is forbidden.
     * <p>
     * </p>
     * Using different empty dictionaries makes Adobe Reader happy again. 
     * </p>
     * @see #testSignTwiceLikeJDNew()
     * @see #testSignTwiceLikeJDNewFirstRevision()
     */
    @Test
    public void testSignTwiceLikeJDNewFixedFile() throws IOException {
        File intermediate = new File(RESULT_FOLDER, "signTwiceFixed-intermediate.pdf");
        File result = new File(RESULT_FOLDER, "signTwiceFixed-result.pdf");
        try ( InputStream resource = getClass().getResourceAsStream("signed2-2-fixed.pdf")) {
            SignMultPDF(Streams.readAll(resource), intermediate.getPath(), null, null, null);
            SignMultPDF(Files.readAllBytes(intermediate.toPath()), result.getPath(), null, null, null);
        }
    }

    /**
     * @see #testSignTwiceLikeJDNew()
     * @see #testSignTwiceLikeJDNewFirstRevision()
     * @see #testSignTwiceLikeJDNewFixedFile()
     * @see #signP7DetachData(InputStream)
     */
    public void SignMultPDF(byte[] pdfFile , String destPath , String name , String fname , String value) {
        IExternalSignatureContainer externalP7DetachSignatureContainer = new IExternalSignatureContainer() {
            @Override
            public byte[] sign(InputStream data) throws GeneralSecurityException {
                byte[] signData = null;
                //signData = signUtil.signP7DetachData(data);
                try {
                    signData = signP7DetachData(data);
                } catch (OperatorCreationException | IOException | CMSException e) {
                    throw new GeneralSecurityException("Error signing", e);
                }

                return signData;
            }

            @Override
            public void modifySigningDictionary(PdfDictionary signDic) {
                signDic.put(PdfName.Filter, PdfName.Adobe_PPKLite);
                signDic.put(PdfName.SubFilter, PdfName.Adbe_pkcs7_detached);
            }
        };

        boolean success = false;

        int estimatedSize = 10000;//300000;

        while (!success) {
            try {

                PdfReader pdfReader = new PdfReader(new ByteArrayInputStream(pdfFile));
                PdfSigner pdfSigner = new PdfSigner(pdfReader, new FileOutputStream(destPath), new StampingProperties().useAppendMode());

                pdfSigner.signExternalContainer(externalP7DetachSignatureContainer, estimatedSize);

                success = true;

            } catch (IOException e) {
                e.printStackTrace();
                estimatedSize += 1000;
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @see #testSignTwiceLikeJDNew()
     * @see #testSignTwiceLikeJDNewFirstRevision()
     * @see #testSignTwiceLikeJDNewFixedFile()
     * @see #SignMultPDF(byte[], String, String, String, String)
     */
    byte[] signP7DetachData(final InputStream data) throws GeneralSecurityException, IOException, CMSException, OperatorCreationException
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


        Store certstore = new JcaCertStore(Arrays.asList(chain));
        Certificate cert = chain[0];

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build(pk);
        CMSSignedDataGenerator generator = new CMSSignedDataGenerator();

        generator.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider("BC").build()).
                build(signer, (X509Certificate) cert));
        generator.addCertificates(certstore);

        
        CMSTypedData cmsdata = new CMSTypedData() {
            
            @Override
            public void write(OutputStream out) throws IOException, CMSException {
                Streams.pipeAll(data, out);
                data.close();
            }
            
            @Override
            public Object getContent() {
                return data;
            }

            @Override
            public ASN1ObjectIdentifier getContentType() {
                return new ASN1ObjectIdentifier(CMSObjectIdentifiers.data.getId());
            }
        };
        CMSSignedData signeddata = generator.generate(cmsdata, false);
        return signeddata.getEncoded();
    }

    /**
     * <a href="https://stackoverflow.com/questions/51812973/multiple-signature-not-working">
     * Multiple signature not working
     * </a>
     * <br/>
     * <a href="https://drive.google.com/open?id=1dOZT_VGnqVe1II_DIiISGcD81be5V6i6">
     * signed.pdf
     * </a>
     * <p>
     * Indeed The OP's code results in warnings that annotations have been modified.
     * Further analysis shows that the annotation indeed have been changed, <b>P</b>
     * entries have been added to their dictionaries, references to the page they're
     * on. 
     * </p>
     * @see #testStampSigned()
     */
    @Test
    public void testSignSignedAgain() throws IOException, GeneralSecurityException {
        try (   InputStream resource = getClass().getResourceAsStream("signed.pdf");
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "signed-again.pdf"))) {
            PdfSigner pdfSigner = new PdfSigner(new PdfReader(resource), result, new StampingProperties().useAppendMode());
            IExternalSignatureContainer external = new ExternalBlankSignatureContainer(PdfName.Adobe_PPKLite, PdfName.Adbe_pkcs7_detached);
            pdfSigner.signExternalContainer(external, 8192);
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/51812973/multiple-signature-not-working">
     * Multiple signature not working
     * </a>
     * <br/>
     * <a href="https://drive.google.com/open?id=1dOZT_VGnqVe1II_DIiISGcD81be5V6i6">
     * signed.pdf
     * </a>
     * <p>
     * This test reduces the issue analyzed in {@link #testSignSignedAgain()} to the
     * inner cause: {@link PdfPage#getAnnotations()} as a side effect adds <b>P</b>
     * entries to the annotation dictionaries, references to the page they're on,
     * and marks them as changed.
     * </p>
     */
    @Test
    public void testStampSigned() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("signed.pdf");
                PdfDocument pdfDocument = new PdfDocument(new PdfReader(resource), new PdfWriter(new File(RESULT_FOLDER, "signed-stamped.pdf")), new StampingProperties().useAppendMode())) {
            pdfDocument.getPage(1).getAnnotations();
        }
    }
}
