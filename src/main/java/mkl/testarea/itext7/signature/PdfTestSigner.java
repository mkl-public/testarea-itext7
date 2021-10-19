package mkl.testarea.itext7.signature;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.List;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PdfSigner.CryptoStandard;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.SignatureUtil;

public class PdfTestSigner {
    public static final String KEYSTORE = "keystores/demo-rsa2048.p12"; 
    public static final char[] PASSWORD = "demo-rsa2048".toCharArray(); 

    public static KeyStore ks = null;
    public static PrivateKey pk = null;
    public static Certificate[] chain = null;

    static {
        BouncyCastleProvider bcp = new BouncyCastleProvider();
        Security.addProvider(bcp);

        try {
            ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(new FileInputStream(KEYSTORE), PASSWORD);
            String alias = (String) ks.aliases().nextElement();
            pk = (PrivateKey) ks.getKey(alias, PASSWORD);
            chain = ks.getCertificateChain(alias);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public final static String FIELD = "FIELD:";
    public final static String TAG = "TAG";
    public final static String NOTAG = "NOTAG";
    public final static String COMPRESS = "COMPRESS";
    public final static String NOCOMPRESS = "NOCOMPRESS";
    public final static List<String> OPTIONS = Arrays.asList(TAG, NOTAG, COMPRESS, NOCOMPRESS);

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        CryptoStandard cryptoStandard = CryptoStandard.CMS;
        boolean tag = true;
        boolean compress = true;
        String fieldName = "Signature";
        for (String arg: args) {
            if (Arrays.stream(CryptoStandard.values()).anyMatch(c -> c.name().equals(arg))) {
                cryptoStandard = CryptoStandard.valueOf(arg);
                System.out.printf("\n### Selected crypto standard %s.\n\n", cryptoStandard);
            } else if (arg.startsWith(FIELD)) {
                fieldName = arg.substring(FIELD.length());
                if (fieldName.length() == 0)
                    fieldName = null;
                System.out.printf("\n### Selected field name %s.\n\n", fieldName);
            } else {
                switch (arg) {
                case TAG:
                    tag = true;
                    System.out.println("\n### Tagged signing activated.\n");
                    break;
                case NOTAG:
                    tag = false;
                    System.out.println("\n### Tagged signing deactivated.\n");
                    break;
                case COMPRESS:
                    compress = true;
                    System.out.println("\n### Compressed signing activated.\n");
                    break;
                case NOCOMPRESS:
                    compress = false;
                    System.out.println("\n### Compressed signing deactivated.\n");
                    break;
                default:
                    System.out.printf("***\n*** %s\n***\n\n", arg);
                    final File file = new File(arg);
                    if (file.exists()) {
                        File target = new File(file.getParent(), file.getName() + "-signed-" + cryptoStandard + ".pdf");
                        new PdfTestSigner(file, cryptoStandard).sign(target, fieldName, tag, compress);
                        System.out.println("   signed successfully.\n");
                    } else
                        System.err.println("!!! File does not exist: " + file);
                    break;
                }
            }
        }
    }

    final File file;
    final CryptoStandard cryptoStandard;

    public PdfTestSigner(File file, CryptoStandard cryptoStandard) {
        this.file = file;
        this.cryptoStandard = cryptoStandard;
    }

    public void sign(File target, String fieldName, boolean tag, boolean compress) throws IOException, GeneralSecurityException {
        try (   PdfReader reader = new PdfReader(file.getAbsolutePath());
                FileOutputStream os = new FileOutputStream(target)  ) {
            PdfSigner signer = new PdfSigner(reader, os, new StampingProperties().useAppendMode()) {
                @Override
                protected PdfDocument initDocument(PdfReader reader, PdfWriter writer, StampingProperties properties) {
                    if (!compress) {
                        writer = new PdfWriter(writer.getOutputStream()) {
                            @Override
                            protected void flushObject(PdfObject pdfObject, boolean canBeInObjStm) {
                                super.flushObject(pdfObject, false);
                            }
                        };
                    }
                    if (tag) {
                        return super.initDocument(reader, writer, properties);
                    } else {
                        return new PdfDocument(reader, writer, properties) {
                            @Override
                            protected void tryInitTagStructure(PdfDictionary str) {
                                structTreeRoot = null;
                                structParentIndex = -1;
                            }
                        };
                    }
                }
            };
            signer.setFieldName(fieldName);
            if (!new SignatureUtil(signer.getDocument()).getBlankSignatureNames().contains(fieldName)) {
                PdfSignatureAppearance appearance = signer.getSignatureAppearance();
                appearance.setPageNumber(1);
                appearance.setPageRect(new Rectangle(36, 748, 144, 780));
            }

            IExternalDigest digest = new BouncyCastleDigest();
            IExternalSignature signature = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, null);
            signer.signDetached(digest, signature, chain, null, null, null, 0, cryptoStandard);
        }
    }
}
