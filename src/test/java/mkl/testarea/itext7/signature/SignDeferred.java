package mkl.testarea.itext7.signature;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Enumeration;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.ExternalBlankSignatureContainer;
import com.itextpdf.signatures.IExternalDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.IExternalSignatureContainer;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PdfSigner.CryptoStandard;

/**
 * @author mkl
 */
public class SignDeferred {
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
     * <p>
     * This test should have tested the OP's signing issue. Unfortunately their code
     * is full of references to unknown variables and types. Thus, currently this test
     * only generates a PDF using the OP's code for generating a document. Signing
     * this results in a properly signing file. Thus, the issue cannot be reproduced.
     * </p>
     */
    @Test
    public void testSignLikeNikhil() throws IOException {
        byte[] pdfFile = createPdfLikeNikhil();
        Files.write(new File(RESULT_FOLDER, "pdfLikeNikhil.pdf").toPath(), pdfFile);

/*
        try (PdfReader pdfReader = new PdfReader(
                prop.getUnsignedpath() + pdfName + WITH_PLACEHOLDER_SIGN_FIELD_PDF_LABEL);
                FileOutputStream signedPdfStream = new FileOutputStream(prop.getSignedpath() + pdfName + ".pdf")) {
            IExternalSignatureContainer container = new PostSignatureContainer(PdfName.Adobe_PPKLite,
                    PdfName.Adbe_pkcs7_detached, pkcs7CmsContainer);
            PdfSigner.signDeferred(new PdfDocument(pdfReader), SIGNATURE_FIELD_NAME, signedPdfStream, container);
            return new FileSystemResource(espProperties.getSignedpath() + pdfName + ".pdf");
*/
    }
/*
    private String getHashToSign(String pdfName, DocumentTO Document) {
        Files.createDirectories(Paths.get(prop.getSignedpath()));
        try (PdfReader pdfReader = new PdfReader(prop.getUnsignedpath() + pdfName + ".pdf");
                FileOutputStream pdfWithSigField = new FileOutputStream(prop.getUnsignedpath()
                        + pdfName + WITH_PLACEHOLDER_SIGN_FIELD_PDF_LABEL)) {
            PdfSigner signer = new PdfSigner(pdfReader, pdfWithSigField, new StampingProperties().useAppendMode());
            signer.setFieldName(SIGNATURE_FIELD_NAME);
            Calendar instance = Calendar.getInstance();
            
            signer.setSignDate(instance);
            signer.setCertificationLevel(PdfSigner.CERTIFIED_NO_CHANGES_ALLOWED);

            PdfSignatureAppearance signatureAppearance = signer.getSignatureAppearance();
            signatureAppearance.setReason("Testing");
            signatureAppearance.setLocation(Document.getSigningPlace());
            

            PreSignatureContainer external = new PreSignatureContainer(PdfName.Adobe_PPKLite,
                    PdfName.Adbe_pkcs7_detached);
            signer.signExternalContainer(external, 15000);

            Files.deleteIfExists(Paths.get(prop.getUnsignedpath() + pdfName + ".pdf"));

            return DigestUtils.sha256Hex(external.getHash());
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
*/
    /**
     * This code uses the OP's code for generating their PDF. As they did not provide
     * the image data, generic images are used. Also their code only created the table,
     * adding it to a {@link Document} is interpretation.
     */
    byte[] createPdfLikeNikhil() throws IOException {
        String IMG1 = "src\\test\\resources\\mkl\\testarea\\itext7\\content\\Oskar.jpg";
        String IMG2 = "src\\test\\resources\\mkl\\testarea\\itext7\\annotate\\Willi-1.jpg";

        try (   ByteArrayOutputStream baos = new ByteArrayOutputStream()    ) {
            try (
                PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos));
                Document document = new Document(pdfDocument);
            ) {
                float [] pointColumnWidths = {130f, 600f};
                Table paraTable = new Table(pointColumnWidths);
                //paraTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
                //paraTable.setFontSize(15);
                paraTable.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD));
                Cell image1 = new Cell();       
                Image img = new Image(ImageDataFactory.create(IMG1));
                
                img.setAutoScale(true);
                Paragraph p=new Paragraph();
                p.add(img);
                image1.setBorder(Border.NO_BORDER);
                image1.add(p);      
                paraTable.addCell(image1);
                
                Cell paracell = new Cell().add(new Paragraph("To Whom It May Concern"));
                paracell.setBorder(Border.NO_BORDER);
                paracell.setTextAlignment(TextAlignment.CENTER);
                paracell.setPaddingTop(35f);
                paraTable.addCell(paracell);
                
                Cell image2 = new Cell();
                ImageData data1 = ImageDataFactory.create(IMG2);
                Image img1 = new Image(data1);
                image2.setBorder(Border.NO_BORDER);
                image2.add(img1.setAutoScale(true));
                image2.add(img1);
                paraTable.addCell(image2);

                document.add(paraTable);
            }
            return baos.toByteArray();
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/71597066/how-to-get-cms-pkcs7-from-pkcs1-zeal-id-integration">
     * How to get CMS (PKCS#7) from PKCS#1 Zeal id integration
     * </a>
     * <p>
     * The OP does not consider doing all the signing service related stuff in the <code>sign</code>
     * method of an {@link IExternalSignature} implementation an option. Instead he prefers to go for
     * deferred signing. In this context the need came up to create PAdES compatible CMS signature
     * containers using {@link PdfPKCS7}. This test does so in a deferred use case. 
     * </p>
     * @see #prepareSignature(InputStream, ByteArrayOutputStream)
     * @see PreSignatureContainer
     * @see #createSignature(InputStream, String)
     * @see ExternalPrecalculatedSignatureContainer
     */
    @Test
    public void testSignLikeGovindB() throws Exception {
        try (
            InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf");
            ByteArrayOutputStream preparedArrayStream = new ByteArrayOutputStream()
        ) {
            IExternalDigest externalDigest = new BouncyCastleDigest();
            String digestAlgorithm = "SHA256";

            // deferred signing, step one
            String base64DocumentHash = prepareSignature(resource, preparedArrayStream);
            // prepare PdfPKCS7 with document hash
            byte[] hash = Base64.decodeBase64(base64DocumentHash);
            PdfPKCS7 sgn = new PdfPKCS7((PrivateKey) null, chain, digestAlgorithm, null, externalDigest, false);
            byte[] sh = sgn.getAuthenticatedAttributeBytes(hash, CryptoStandard.CADES, null, null);
            // retrieve signature bytes
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initSign(pk);
            sig.update(sh);
            byte[] extSignature = sig.sign();
            // finalize PdfPKCS7 with signature bytes
            sgn.setExternalDigest(extSignature, null, "RSA");
            byte[] encodedSig = sgn.getEncodedPKCS7(hash, CryptoStandard.CADES, null, null, null);
            // deferred signing, step two
            try (ByteArrayInputStream preparedInputStream = new ByteArrayInputStream(preparedArrayStream.toByteArray())) {
                createSignature(preparedInputStream , Base64.encodeBase64String(encodedSig));
            }
        }
    }

    /** @see #testSignLikeGovindB() */
    static String prepareSignature(InputStream in, ByteArrayOutputStream preparedArrayStream) throws IOException, GeneralSecurityException {
        PdfReader reader = new PdfReader(in);
        Rectangle rect = new Rectangle(36, 648, 200, 100);
        PdfSigner signer = new PdfSigner(reader, preparedArrayStream,  new StampingProperties().useAppendMode());

        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        appearance.setPageRect(rect)
                  .setPageNumber(1)
                  .setLocation("EU")
                  .setReason("Test");
        signer.setFieldName("testing");

        PreSignatureContainer external = new PreSignatureContainer(PdfName.Adobe_PPKLite, PdfName.ETSI_CAdES_DETACHED);

        signer.signExternalContainer(external, 8192);
        String hash = Base64.encodeBase64String(external.getHash());
        return hash;
    }

    /** @see #testSignLikeGovindB() */
    static class PreSignatureContainer implements IExternalSignatureContainer {
        private PdfDictionary sigDic;
        private byte hash[];

        public PreSignatureContainer(PdfName filter, PdfName subFilter) {
            sigDic = new PdfDictionary();
            sigDic.put(PdfName.Filter, filter);
            sigDic.put(PdfName.SubFilter, subFilter);
        }

        @Override
        public byte[] sign(InputStream data) throws GeneralSecurityException {
            String hashAlgorithm = "SHA256";
            BouncyCastleDigest digest = new BouncyCastleDigest();

            try {
                this.hash = DigestAlgorithms.digest(data, digest.getMessageDigest(hashAlgorithm));
            } catch (IOException e) {
                throw new GeneralSecurityException("PreSignatureContainer signing exception", e);
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

    /** @see #testSignLikeGovindB() */
    static void createSignature(InputStream in, String signatures) throws Exception {
        byte[] decodeSignature = Base64.decodeBase64(signatures);
        PdfReader reader =  null;
        reader = new PdfReader(in);
        FileOutputStream outputStream = new FileOutputStream(new File(RESULT_FOLDER, "newZeal.pdf"));
        PdfSigner.signDeferred(new PdfDocument(reader), "testing", outputStream, new ExternalPrecalculatedSignatureContainer(decodeSignature));
        outputStream.close();
    }

    /** @see #testSignLikeGovindB() */
    static class ExternalPrecalculatedSignatureContainer extends ExternalBlankSignatureContainer
    {
        byte[] cmsSignatureContents;

        public ExternalPrecalculatedSignatureContainer(byte[] cmsSignatureContents) {
            super(new PdfDictionary());
            this.cmsSignatureContents = cmsSignatureContents;
        }

        @Override
        public  byte[] sign(InputStream data) throws CertificateException, InvalidKeyException, NoSuchProviderException, NoSuchAlgorithmException {
            return cmsSignatureContents;
        }
    }

}
