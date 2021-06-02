package mkl.testarea.itext7.signature;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.IExternalSignatureContainer;
import com.itextpdf.signatures.PdfSignature;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.SignatureUtil;

/**
 * <a href="https://stackoverflow.com/questions/67770841/c-sharp-itext7-signed-pdf-signature-is-invalid-in-foxit-pdf-reqader-but-valid-in">
 * C# Itext7 signed pdf signature is invalid in Foxit PDF Reqader but valid in Acrobat reader
 * </a>
 * <p>
 * This tool sets the signature algorithm in the CMS signature container of
 * the outermost signature of the given PDF. Currently it is hardcoded to set
 * it to SHA256withPLAIN-ECDSA (to match the case at hand) but if need be it
 * can easily be extended to arbitrary algorithms.
 * </p>
 * 
 * @author mkl
 */
public class SetSignatureAlgorithm {
    public static void main(String[] args) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        DefaultSignatureAlgorithmIdentifierFinder algorithmIdentifierFinder = new DefaultSignatureAlgorithmIdentifierFinder();
        AlgorithmIdentifier algorithmIdentifier = algorithmIdentifierFinder.find("SHA256withPLAIN-ECDSA");
//        AlgorithmIdentifier algorithmIdentifier = algorithmIdentifierFinder.find("SHA256withECDSA");

        for (String arg: args) {
            switch (arg) {
            default:
                System.out.printf("***\n*** %s\n***\n\n", arg);
                final File file = new File(arg);
                if (file.exists()) {
                    File target = new File(file.getParent(), file.getName() + "-fixed.pdf");
                    new SetSignatureAlgorithm(algorithmIdentifier).setInOutermostSignature(new FileInputStream(file), new FileOutputStream(target));
                    System.out.println("   fixed successfully.\n");
                } else
                    System.err.println("!!! File does not exist: " + file);
                break;
            }
        }
    }

    public SetSignatureAlgorithm(AlgorithmIdentifier algorithmIdentifier) throws NoSuchFieldException, SecurityException {
        this.algorithmIdentifier = algorithmIdentifier;
        this.digEncryptionAlgorithmField = SignerInfo.class.getDeclaredField("digEncryptionAlgorithm");
        this.digEncryptionAlgorithmField.setAccessible(true);
        this.encryptionAlgorithmField = SignerInformation.class.getDeclaredField("encryptionAlgorithm");
        this.encryptionAlgorithmField.setAccessible(true);
    }

    public void setInOutermostSignature(InputStream source, OutputStream result) throws IOException, CMSException, GeneralSecurityException, IllegalAccessException {
        try (   PdfReader pdfReader = new PdfReader(source);
                PdfDocument pdfDocument = new PdfDocument(pdfReader)    ) {
            SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);
            List<String> signatureNames = signatureUtil.getSignatureNames();
            if (signatureNames == null || signatureNames.isEmpty()) {
                throw new IOException("No signed signature field found.");
            }
            String fieldName = signatureNames.get(signatureNames.size() - 1);

            PdfSignature pdfSignature = signatureUtil.getSignature(fieldName);
            byte[] cmsBytes = pdfSignature.getContents().getValueBytes();
            CMSSignedData cmsSignedData = fixSignedData(new CMSSignedData(cmsBytes));

            IExternalSignatureContainer container = new IExternalSignatureContainer() {
                @Override
                public byte[] sign(InputStream data) throws GeneralSecurityException {
                    try {
                        return cmsSignedData.getEncoded("DER");
                    } catch (IOException e) {
                        throw new GeneralSecurityException(e);
                    }
                }

                @Override
                public void modifySigningDictionary(PdfDictionary signDic) {
                }
            };
            PdfSigner.signDeferred(pdfDocument, fieldName, result, container);
        }
    }

    CMSSignedData fixSignedData(CMSSignedData cmsSignedData) throws IllegalAccessException {
        Collection<SignerInformation> fixedInfos = new ArrayList<>();
        for (SignerInformation signerInformation : cmsSignedData.getSignerInfos()) {
            encryptionAlgorithmField.set(signerInformation, algorithmIdentifier);
            SignerInfo signerInfo = signerInformation.toASN1Structure();
            digEncryptionAlgorithmField.set(signerInfo, algorithmIdentifier);
            fixedInfos.add(signerInformation);
        }
        return CMSSignedData.replaceSigners(cmsSignedData, new SignerInformationStore(fixedInfos));
    }

    final AlgorithmIdentifier algorithmIdentifier;
    final Field digEncryptionAlgorithmField;
    final Field encryptionAlgorithmField;
}
