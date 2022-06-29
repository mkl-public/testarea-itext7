package mkl.testarea.itext7.signature;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSAbsentContent;
import org.bouncycastle.cms.CMSAttributeTableGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.DefaultSignedAttributeTableGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.commons.utils.Base64;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.IExternalSignatureContainer;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;

/**
 * @author mklink
 *
 */
public class SignLikeNikhil {
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
     * <a href="https://stackoverflow.com/questions/72578658/invalidating-the-signature-when-the-pdf-contains-image-but-works-fine-without-t">
     * Invalidating the signature when the pdf contains image, but works fine without the image in itext
     * </a>
     * <br/>
     * <a href="https://github.com/nikhilkpuhran/sample/blob/master/761bd204dcbd42a6ac7a03cbd77b86f5signed.pdf">
     * 761bd204dcbd42a6ac7a03cbd77b86f5signed.pdf
     * </a> without signing revision as "761bd204dcbd42a6ac7a03cbd77b86f5.pdf"
     * <p>
     * This test makes Nikhil's code in his class
     * <a href="https://github.com/nikhilkpuhran/sample/blob/master/PDFServiceImpl.java">PDFServiceImpl</a>
     * runnable without all the unknown external dependencies.
     * </p>
     * <p>
     * In the end, though, I cannot reproduce the issue, Adobe does not report
     * that the document has been altered or corrupted.
     * </p>
     */
    @Test
    public void testSignLikeNikhil() throws IOException, GeneralSecurityException, DecoderException, OperatorException, CMSException {
        String pdfName = "761bd204dcbd42a6ac7a03cbd77b86f5";

        String sha256Hex = getHashToSign(pdfName);


        // we don't have the signed byte ranges, only their hash, so the msg is a dummy
        CMSTypedData msg = new CMSAbsentContent();
        // and we have to inject the precalculated hash
        AttributeTable attributeTable = new AttributeTable(new Attribute(CMSAttributes.messageDigest, new DERSet(new DEROctetString(Hex.decodeHex(sha256Hex)))));
        CMSAttributeTableGenerator signedDataGenerator = new DefaultSignedAttributeTableGenerator(attributeTable);

        ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256withRSA").build(pk);

        CMSSignedDataGenerator gen = new CMSSignedDataGenerator();

        gen.addSignerInfoGenerator(
                new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider("BC").build())
                        .setSignedAttributeGenerator(signedDataGenerator)
                        .build(contentSigner, (X509Certificate)chain[0]));

        gen.addCertificates(new JcaCertStore(Arrays.asList(chain)));

        CMSSignedData sigData = gen.generate(msg, false);

        String base64Cms = org.apache.commons.codec.binary.Base64.encodeBase64String(sigData.getEncoded());


        signPdf(base64Cms, pdfName);
    }

    //
    // Methods, constants,  and classes from Nikhil's PDFServiceImpl
    // <https://github.com/nikhilkpuhran/sample/blob/master/PDFServiceImpl.java>
    // with unknown/unneeded externals removed
    //
    private static final Logger LOGGER = LoggerFactory.getLogger(SignLikeNikhil.class); // LoggerFactory.getLogger(PDFServiceImpl.class);

    final static String SIGNED_PATH = "target/test-outputs/signature/"; // espProperties.getSignedpath()
    final static String UNSIGNED_PATH = "target/test-outputs/signature/"; // espProperties.getUnsignedpath()
    final static String SIGNING_PLACE = "Pune"; // hfrDocument.getSigningPlace()

    static final String SIGNATURE_FIELD_NAME = "digiSign1";
    static final String WITH_PLACEHOLDER_SIGN_FIELD_PDF_LABEL = "_with_placeholder_sign_field.pdf";

    String getHashToSign(String pdfName) throws IOException, GeneralSecurityException {
        Files.createDirectories(Paths.get(SIGNED_PATH));
        try (
            InputStream resource = getClass().getResourceAsStream(pdfName + ".pdf");
            PdfReader pdfReader = new PdfReader(resource);
            FileOutputStream pdfWithSigField = new FileOutputStream(UNSIGNED_PATH
                    + pdfName + WITH_PLACEHOLDER_SIGN_FIELD_PDF_LABEL)
        ) {
            PdfSigner signer = new PdfSigner(pdfReader, pdfWithSigField, new StampingProperties().useAppendMode());
            signer.setFieldName(SIGNATURE_FIELD_NAME);
            Calendar instance = Calendar.getInstance();
            instance.add(Calendar.MINUTE, 10);
            signer.setSignDate(instance);
            signer.setCertificationLevel(PdfSigner.CERTIFIED_NO_CHANGES_ALLOWED);

            PdfSignatureAppearance signatureAppearance = signer.getSignatureAppearance();
            signatureAppearance.setReason("Testing");
            signatureAppearance.setLocation(SIGNING_PLACE);
            

            PreSignatureContainer external = new PreSignatureContainer(PdfName.Adobe_PPKLite,
                    PdfName.Adbe_pkcs7_detached);
            signer.signExternalContainer(external, 15000);

            return DigestUtils.sha256Hex(external.getHash());
        }
    }

    private class PreSignatureContainer implements IExternalSignatureContainer {
        private PdfDictionary sigDic;
        private byte[] hash;

        public PreSignatureContainer(PdfName filter, PdfName subFilter) {
            sigDic = new PdfDictionary();
            sigDic.put(PdfName.Filter, filter);
            sigDic.put(PdfName.SubFilter, subFilter);
        }

        @Override
        public byte[] sign(InputStream data) {
            try {
                this.hash = StreamUtil.inputStreamToArray(data);
            } catch (IOException e) {
                LOGGER.error(ExceptionUtils.getStackTrace(e));
            }
            return new byte[0];
        }

        @Override
        public void modifySigningDictionary(PdfDictionary signDic) {
            signDic.putAll(sigDic);
        }

        public byte[] getHash() {
            return hash;
        }
    }

    public File signPdf(String pkcs7CmsContainer, String pdfName) throws IOException, GeneralSecurityException {
        try (
            PdfReader pdfReader = new PdfReader(
                UNSIGNED_PATH + pdfName + WITH_PLACEHOLDER_SIGN_FIELD_PDF_LABEL);
            FileOutputStream signedPdfStream = new FileOutputStream(SIGNED_PATH + pdfName + ".pdf");
            PdfDocument pd=new PdfDocument(pdfReader)
        ) {
            signedPdfStream.flush();

            IExternalSignatureContainer container = new PostSignatureContainer(PdfName.Adobe_PPKLite,
                    PdfName.Adbe_pkcs7_detached, pkcs7CmsContainer);

            PdfSigner.signDeferred(pd, SIGNATURE_FIELD_NAME, signedPdfStream, container);

            return new File(SIGNED_PATH + pdfName + ".pdf");
        }
    }

    private class PostSignatureContainer implements IExternalSignatureContainer {
        private PdfDictionary sigDic;
        private String pkcs7CmsContainer;

        public PostSignatureContainer(PdfName filter, PdfName subFilter, String pkcs7CmsContainer) {
            sigDic = new PdfDictionary();
            sigDic.put(PdfName.Filter, filter);
            sigDic.put(PdfName.SubFilter, subFilter);
            this.pkcs7CmsContainer = pkcs7CmsContainer;
        }

        @Override
        public byte[] sign(InputStream data) throws GeneralSecurityException {
            return Base64.decode(this.pkcs7CmsContainer);
        }

        @Override
        public void modifySigningDictionary(PdfDictionary signDic) {
            signDic.putAll(sigDic);
        }
    }

}
