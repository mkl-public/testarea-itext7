/**
 * 
 */
package mkl.testarea.itext7.signature;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.List;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.CrlClientOnline;
import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.IOcspClient;
import com.itextpdf.signatures.LtvVerification;
import com.itextpdf.signatures.OcspClientBouncyCastle;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.SignatureUtil;

/**
 * @author mklink
 *
 */
public class EnableLtv
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
     * <a href="http://stackoverflow.com/questions/42899185/itext-7-adding-ltv-to-existing-signature">
     * iText 7 adding ltv to existing signature
     * </a>
     * <p>
     * This test applies the method {@link #addLtvNoTS(InputStream, OutputStream, IOcspClient, ICrlClient, LtvVerification.Level, LtvVerification.Level)}
     * to a simple D-Trust signed PDF with no embedded LTV information. That method does only
     * add LTV information for the immediate PDF signatures and document timestamps, not for
     * already embedded or now added OCSP response or CRL signatures. As D-Trust OCSP responses
     * are signed by certificates which need own LTV information while the CRL is signed by
     * a signature which doesn't, we have to use CRLs all over here to get a "LTV-enabled"
     * document. 
     * </p>
     * <p>
     * Furthermore we explicitly have to give a link to the CRL as D-Trust mentions two CRL
     * distribution points, the first one being reachable via LDAP, and iText only uses the
     * first distribution point but does not support LDAP.
     * </p>
     */
    @Test
    public void testLtvEnableBlankSigned() throws IOException, GeneralSecurityException
    {
        try (   InputStream resource = getClass().getResourceAsStream("BLANK-signed.pdf");
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "BLANK-signed-enabled.pdf")))
        {
            addLtvNoTS(resource, result, new OcspClientBouncyCastle(null), new CrlClientOnline("http://crl.d-trust.net/crl/d-trust_qualified_ca_3_2014.crl"), LtvVerification.Level.CRL, LtvVerification.Level.CRL);
        }
    }

    /**
     * <a href="http://stackoverflow.com/questions/42899185/itext-7-adding-ltv-to-existing-signature">
     * iText 7 adding ltv to existing signature
     * </a>
     * <p>
     * This is the iText 7 equivalent to the iText 5 `addLtvNoTS` the OP refers
     * to in a comment and which originally was posted on stack overflow as
     * <a href="http://stackoverflow.com/a/27906587/1729265">this answer</a>.
     * </p>
     */
    void addLtvNoTS(InputStream src, OutputStream dest, IOcspClient ocsp, ICrlClient crl, LtvVerification.Level timestampLevel, LtvVerification.Level signatureLevel) throws IOException, GeneralSecurityException 
    {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest), new StampingProperties().useAppendMode());

        LtvVerification v = new LtvVerification(pdfDoc);
        SignatureUtil signatureUtil = new SignatureUtil(pdfDoc);

        List<String> names = signatureUtil.getSignatureNames();
        String sigName = names.get(names.size() - 1);

        PdfPKCS7 pkcs7 = signatureUtil.verifySignature(sigName);

        if (pkcs7.isTsp())
        {
            v.addVerification(sigName, ocsp, crl, LtvVerification.CertificateOption.WHOLE_CHAIN,
                    timestampLevel, LtvVerification.CertificateInclusion.YES);
        }
        else
        {
            for (String name : names)
            {
                v.addVerification(name, ocsp, crl, LtvVerification.CertificateOption.WHOLE_CHAIN,
                        signatureLevel, LtvVerification.CertificateInclusion.YES);
                v.merge();
            }
        }

        pdfDoc.close();
    }
}
