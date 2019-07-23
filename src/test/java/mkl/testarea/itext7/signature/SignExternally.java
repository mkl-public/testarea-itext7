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
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignatureContainer;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSignatureAppearance.RenderingMode;
import com.itextpdf.signatures.PdfSigner;

/**
 * @author mkl
 */
public class SignExternally {
    final static File RESULT_FOLDER = new File("target/test-outputs", "signature");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
    }

    /**
     * <a href="https://stackoverflow.com/questions/57140446/my-signature-structure-disturbs-on-embedding-signature-using-itext7">
     * My signature structure disturbs on embedding signature using itext7
     * </a>
     * <br/>
     * <a href="https://www.dropbox.com/s/h72u360rl5iy6fq/SigFieldDoc%20-%20AfterSign.pdf?dl=0">
     * SigFieldDoc - AfterSign.pdf
     * </a>, the image in the signature therein is exported here as "Hand signature.jpg".
     * <p>
     * This test shows how to easily use an external signing service that
     * returns CMS signature containers.
     * </p>
     */
    @Test
    public void testSignWithExternalCmsService() throws GeneralSecurityException, IOException {
        IExternalSignatureContainer externalServiceSignatureContainer = new IExternalSignatureContainer() {
            @Override
            public byte[] sign(InputStream data) throws GeneralSecurityException {
                // Call your external signing service to create a CMS signature container
                // for the data in the InputStream

                // Depending on your API to access that service you may either be able to
                // directly call it with the stream
             // return YOUR_SIGNING_API_CALL_FOR_STREAM(data);
                // (or a byte[] generated from the stream contents)
             // return YOUR_SIGNING_API_CALL_FOR_ARRAY(StreamUtil.inputStreamToArray(data));
                // as parameter, or you may first have to hash the data yourself
                // (e.g. as follows) and send your hash to the service.
                String hashAlgorithm = "SHA256";
                BouncyCastleDigest digest = new BouncyCastleDigest();

                try
                {
                    byte[] hash = DigestAlgorithms.digest(data, digest.getMessageDigest(hashAlgorithm));
                 // return YOUR_SIGNING_API_CALL_FOR_HASH(hash)
                }
                catch (IOException e)
                {
                    throw new GeneralSecurityException("PreSignatureContainer signing exception", e);
                }

                // dummy
                return new byte[0];
            }

            @Override
            public void modifySigningDictionary(PdfDictionary signDic) {
                signDic.put(PdfName.Filter, PdfName.Adobe_PPKLite);
                signDic.put(PdfName.SubFilter, PdfName.Adbe_pkcs7_detached);
            }
        };

        File destPath = new File(RESULT_FOLDER, "test-external.pdf");
        try (   InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf");
                InputStream imageResource = getClass().getResourceAsStream("Hand signature.jpg")) {
            PdfReader pdfReader = new PdfReader(resource);
            PdfSigner pdfSigner = new PdfSigner(pdfReader, new FileOutputStream(destPath), new StampingProperties().useAppendMode());

            pdfSigner.setFieldName("Signature1");

            ImageData imageData = ImageDataFactory.create(StreamUtil.inputStreamToArray(imageResource));

            PdfSignatureAppearance sigAppearance = pdfSigner.getSignatureAppearance();
            sigAppearance.setContact("ContactInfo");
            sigAppearance.setLocation("Location");
            sigAppearance.setPageNumber(1);
            sigAppearance.setPageRect(new Rectangle(100, 500, imageData.getWidth()/2, imageData.getHeight()/2));
            sigAppearance.setReason("SigningReason");
            sigAppearance.setSignatureGraphic(imageData);
            sigAppearance.setRenderingMode(RenderingMode.GRAPHIC);
            sigAppearance.setSignatureCreator("Malik");

            int estimatedSize = 12000;
            pdfSigner.signExternalContainer(externalServiceSignatureContainer, estimatedSize);
        }
    }
}
