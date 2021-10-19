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

import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.ITSAClient;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PdfSigner.CryptoStandard;
import com.itextpdf.signatures.PrivateKeySignature;

/**
 * @author mkl
 */
public class EncryptAndSign {
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
     * <a href="https://stackoverflow.com/questions/49790146/lock-pdf-using-itext">
     * Lock pdf using iText
     * </a>
     * <p>
     * This test show how to create an encrypted and signed PDF.
     * </p>
     */
    @Test
    public void testEncryptAndSignLefterisBab() throws IOException, GeneralSecurityException {
        File encryptedFile = new File(RESULT_FOLDER, "LefterisBab-encrypted.pdf");
        File signedFile = new File(RESULT_FOLDER, "LefterisBab-encrypted-signed.pdf");
        byte[] password = "test".getBytes();

        try (   InputStream resourceStream = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf");
                OutputStream encryptedResult = new FileOutputStream(encryptedFile)  ) {
            encrypt(resourceStream, encryptedResult, password);
        }

        try (   InputStream encryptedSource = new FileInputStream(encryptedFile);
                OutputStream signedResult = new FileOutputStream(signedFile)) {
            sign(encryptedSource, signedResult, "Signature", CryptoStandard.CADES, 0, false, password);
        }
    }

    // @see #testEncryptAndSignLefterisBab()
    void encrypt(InputStream source, OutputStream target, byte[] password) throws IOException {
        PdfReader reader = new PdfReader(source);
        PdfWriter writer = new PdfWriter(target, new WriterProperties().setStandardEncryption(null, password,
                EncryptionConstants.ALLOW_PRINTING, EncryptionConstants.ENCRYPTION_AES_128 | EncryptionConstants.DO_NOT_ENCRYPT_METADATA));
        new PdfDocument(reader, writer).close();
    }

    // @see #testEncryptAndSignLefterisBab()
    void sign(InputStream original, OutputStream result, String name, CryptoStandard subfilter,
            int certificationLevel, boolean isAppendMode, byte[] password) throws IOException, GeneralSecurityException {
        String reason = "Just another illusionary reason";
        String location = "Right around the corner";
        boolean setReuseAppearance = false;
        String digestAlgorithm = "SHA512";

        PdfReader reader = new PdfReader(original, new ReaderProperties().setPassword(password));
        PdfSigner signer = new PdfSigner(reader, result, isAppendMode ? new StampingProperties().useAppendMode() : new StampingProperties());

        signer.setCertificationLevel(certificationLevel);

        // Creating the appearance
        signer.getSignatureAppearance()
              .setReason(reason)
              .setLocation(location)
              .setReuseAppearance(setReuseAppearance);

        signer.setFieldName(name);
        ITSAClient tsc = null;
        // Creating the signature
        IExternalSignature pks = new PrivateKeySignature(pk, digestAlgorithm, BouncyCastleProvider.PROVIDER_NAME);
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, tsc, 0, subfilter);
    }
}
