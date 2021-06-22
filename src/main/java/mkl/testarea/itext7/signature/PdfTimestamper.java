package mkl.testarea.itext7.signature;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.List;

import org.bouncycastle.cms.CMSSignedData;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.IExternalSignatureContainer;
import com.itextpdf.signatures.ITSAClient;
import com.itextpdf.signatures.PdfSignature;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.SignatureUtil;
import com.itextpdf.signatures.TSAClientBouncyCastle;

/**
 * <a href="https://stackoverflow.com/questions/68056676/add-inner-timestamp-after-the-signing">
 * Add signature timestamp after the signing
 * </a>
 * <p>
 * This utility class allows to extend the outermost signature
 * of a PDF with a signature timestamp.
 * </p>
 * 
 * @author mkl
 */
public class PdfTimestamper extends CmsTimestamper {
    public static void main(String[] args) throws Exception {
        ITSAClient tsaClient = null;
        String fileName = null;
        if (args.length == 2) {
            tsaClient = new TSAClientBouncyCastle(args[0]);
            fileName = args[1];
        } else if (args.length == 4) {
            tsaClient = new TSAClientBouncyCastle(args[0], args[1], args[2]);
            fileName = args[3];
        } else {
            System.out.println("Arguments: <tsa url> [<tsa user> <tsa password>] <pdf file>");
            System.exit(-1);
        }

        PdfTimestamper pdfTimestamper = new PdfTimestamper(tsaClient);

        File source = new File(fileName);
        File target = new File(source.getParent(), source.getName() + "-timestamped.pdf");
        pdfTimestamper.timestamp(source, target);
    }

    public PdfTimestamper(ITSAClient tsaClient) {
        super(tsaClient);
    }

    public void timestamp(File source, File target) throws Exception {
        try (   PdfReader pdfReader = new PdfReader(source);
                PdfDocument pdfDocument = new PdfDocument(pdfReader);
                OutputStream outputStream = new FileOutputStream(target);   ) {
            SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);
            List<String> signatureNames = signatureUtil.getSignatureNames();
            if (signatureNames == null || signatureNames.isEmpty()) {
                System.out.println("   PDF has no AcroForm signature.");
                return;
            }
            String fieldName = signatureNames.get(signatureNames.size() - 1);

            PdfSignature pdfSignature = signatureUtil.getSignature(fieldName);
            byte[] signature = pdfSignature.getContents().getValueBytes();
            CMSSignedData cmsSignedData = new CMSSignedData(signature);
            cmsSignedData = timestamp(cmsSignedData);
            byte[] signatureWithTimestamp = cmsSignedData.getEncoded("DER");

            IExternalSignatureContainer injector = new IExternalSignatureContainer() {
                @Override
                public byte[] sign(InputStream data) throws GeneralSecurityException {
                    return signatureWithTimestamp;
                }

                @Override
                public void modifySigningDictionary(PdfDictionary signDic) {
                }
            };

            PdfSigner.signDeferred(pdfDocument, fieldName, outputStream, injector);
        }
    }
}
