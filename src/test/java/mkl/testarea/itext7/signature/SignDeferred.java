package mkl.testarea.itext7.signature;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

/**
 * @author mkl
 */
public class SignDeferred {
    final static File RESULT_FOLDER = new File("target/test-outputs", "signature");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
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
}
