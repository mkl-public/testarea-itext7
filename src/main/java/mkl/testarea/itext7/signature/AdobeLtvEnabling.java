package mkl.testarea.itext7.signature;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.X509CertParser;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.x509.util.StreamParsingException;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.source.ByteBuffer;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.CompressionConstants;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfCatalog;
import com.itextpdf.kernel.pdf.PdfDate;
import com.itextpdf.kernel.pdf.PdfDeveloperExtension;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.IOcspClient;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.PdfSignature;
import com.itextpdf.signatures.SignatureUtil;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;

/**
 * <a href="https://stackoverflow.com/questions/51370965/how-can-i-add-pades-ltv-using-itext">
 * how can I add PAdES-LTV using itext
 * </a>
 * <br/>
 * <a href="https://stackoverflow.com/questions/51639464/itext7-ltvverification-addverification-not-enabling-ltv">
 * iText7 LtvVerification.addVerification not enabling LTV
 * </a>
 * <p>
 * This class adds LTV information to a signed PDF to make it LTV enabled
 * as reported by Adobe Acrobat.
 * </p>
 * <p>
 * It has originally been written for iText 5 in the context of the former
 * question. In the context of the latter one it has been ported to iText 7.
 * As a side effect some iText-5-isms may be contained in this code.
 * </p>
 * 
 * @author mkl
 */
public class AdobeLtvEnabling {
    /**
     * Use this constructor with a {@link PdfDocument} in append mode. Otherwise
     * the existing signatures will be damaged.
     */
    public AdobeLtvEnabling(PdfDocument pdfDocument) {
        this.pdfDocument = pdfDocument;
    }

    /**
     * Call this method to have LTV information added to the {@link PdfDocument}
     * given in the constructor.
     */
    public void enable(IOcspClient ocspClient, ICrlClient crlClient) throws OperatorException, GeneralSecurityException, IOException, StreamParsingException, OCSPException {
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        List<String> names = signatureUtil.getSignatureNames();
        for (String name : names) {
            PdfPKCS7 pdfPKCS7 = signatureUtil.readSignatureData(name, BouncyCastleProvider.PROVIDER_NAME);
            PdfSignature sig = signatureUtil.getSignature(name);
            List<X509Certificate> certificatesToCheck = new ArrayList<>();
            certificatesToCheck.add(pdfPKCS7.getSigningCertificate());
            while (!certificatesToCheck.isEmpty()) {
                X509Certificate certificate = certificatesToCheck.remove(0);
                addLtvForChain(certificate, ocspClient, crlClient, getSignatureHashKey(sig));
            }
        }

        outputDss();
    }

    //
    // the actual LTV enabling methods
    //
    void addLtvForChain(X509Certificate certificate, IOcspClient ocspClient, ICrlClient crlClient, PdfName key) throws GeneralSecurityException, IOException, StreamParsingException, OperatorCreationException, OCSPException {
        ValidationData validationData = new ValidationData();

        while (certificate != null) {
            System.out.println(certificate.getSubjectX500Principal().getName());
            X509Certificate issuer = getIssuerCertificate(certificate);
            validationData.certs.add(certificate.getEncoded());
            byte[] ocspResponse = ocspClient.getEncoded(certificate, issuer, null);
            if (ocspResponse != null) {
                System.out.println("  with OCSP response");
                validationData.ocsps.add(ocspResponse);
                X509Certificate ocspSigner = getOcspSignerCertificate(ocspResponse);
                if (ocspSigner != null) {
                    System.out.printf("  signed by %s\n", ocspSigner.getSubjectX500Principal().getName());
                }
                addLtvForChain(ocspSigner, ocspClient, crlClient, getOcspHashKey(ocspResponse));
            } else {
               Collection<byte[]> crl = crlClient.getEncoded(certificate, null);
               if (crl != null && !crl.isEmpty()) {
                   System.out.printf("  with %s CRLs\n", crl.size());
                   validationData.crls.addAll(crl);
                   for (byte[] crlBytes : crl) {
                       addLtvForChain(null, ocspClient, crlClient, getCrlHashKey(crlBytes));
                   }
               }
            }
            certificate = issuer;
        }

        validated.put(key, validationData);
    }

    void outputDss() throws IOException {
        PdfDictionary dss = new PdfDictionary();
        PdfDictionary vrim = new PdfDictionary();
        PdfArray ocsps = new PdfArray();
        PdfArray crls = new PdfArray();
        PdfArray certs = new PdfArray();

        PdfCatalog catalog = pdfDocument.getCatalog();
        if (pdfDocument.getPdfVersion().compareTo(PdfVersion.PDF_2_0) < 0) {
            catalog.addDeveloperExtension(PdfDeveloperExtension.ESIC_1_7_EXTENSIONLEVEL5);
            catalog.addDeveloperExtension(new PdfDeveloperExtension(PdfName.ADBE, new PdfName("1.7"), 8));
        }

        for (PdfName vkey : validated.keySet()) {
            PdfArray ocsp = new PdfArray();
            PdfArray crl = new PdfArray();
            PdfArray cert = new PdfArray();
            PdfDictionary vri = new PdfDictionary();
            for (byte[] b : validated.get(vkey).crls) {
                PdfStream ps = new PdfStream(b);
                ps.setCompressionLevel(CompressionConstants.DEFAULT_COMPRESSION);
                ps.makeIndirect(pdfDocument);
                crl.add(ps);
                crls.add(ps);
                crls.setModified();
            }
            for (byte[] b : validated.get(vkey).ocsps) {
                b = buildOCSPResponse(b);
                PdfStream ps = new PdfStream(b);
                ps.setCompressionLevel(CompressionConstants.DEFAULT_COMPRESSION);
                ps.makeIndirect(pdfDocument);
                ocsp.add(ps);
                ocsps.add(ps);
                ocsps.setModified();
            }
            for (byte[] b : validated.get(vkey).certs) {
                PdfStream ps = new PdfStream(b);
                ps.setCompressionLevel(CompressionConstants.DEFAULT_COMPRESSION);
                ps.makeIndirect(pdfDocument);
                cert.add(ps);
                certs.add(ps);
                certs.setModified();
            }
            if (ocsp.size() > 0) {
                ocsp.makeIndirect(pdfDocument);
                vri.put(PdfName.OCSP, ocsp);
            }
            if (crl.size() > 0) {
                crl.makeIndirect(pdfDocument);
                vri.put(PdfName.CRL, crl);
            }
            if (cert.size() > 0) {
                cert.makeIndirect(pdfDocument);
                vri.put(PdfName.Cert, cert);
            }
            vri.put(PdfName.TU, new PdfDate().getPdfObject());
            vri.makeIndirect(pdfDocument);
            vrim.put(vkey, vri);
        }
        vrim.makeIndirect(pdfDocument);
        vrim.setModified();
        dss.put(PdfName.VRI, vrim);
        if (ocsps.size() > 0) {
            ocsps.makeIndirect(pdfDocument);
            dss.put(PdfName.OCSPs, ocsps);
        }
        if (crls.size() > 0) {
            crls.makeIndirect(pdfDocument);
            dss.put(PdfName.CRLs, crls);
        }
        if (certs.size() > 0) {
            certs.makeIndirect(pdfDocument);
            dss.put(PdfName.Certs, certs);
        }

        dss.makeIndirect(pdfDocument);
        dss.setModified();
        catalog.put(PdfName.DSS, dss);
    }

    //
    // VRI signature hash key calculation
    //
    static PdfName getCrlHashKey(byte[] crlBytes) throws NoSuchAlgorithmException, IOException, CRLException, CertificateException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509CRL crl = (X509CRL)cf.generateCRL(new ByteArrayInputStream(crlBytes));
        byte[] signatureBytes = crl.getSignature();
        DEROctetString octetString = new DEROctetString(signatureBytes);
        byte[] octetBytes = octetString.getEncoded();
        byte[] octetHash = hashBytesSha1(octetBytes);
        PdfName octetName = new PdfName(convertToHex(octetHash));
        return octetName;
    }

    static PdfName getOcspHashKey(byte[] basicResponseBytes) throws NoSuchAlgorithmException, IOException {
        BasicOCSPResponse basicResponse = BasicOCSPResponse.getInstance(basicResponseBytes);
        byte[] signatureBytes = basicResponse.getSignature().getBytes();
        DEROctetString octetString = new DEROctetString(signatureBytes);
        byte[] octetBytes = octetString.getEncoded();
        byte[] octetHash = hashBytesSha1(octetBytes);
        PdfName octetName = new PdfName(convertToHex(octetHash));
        return octetName;
    }

    static PdfName getSignatureHashKey(PdfSignature sig) throws NoSuchAlgorithmException, IOException {
        PdfString contents = sig.getContents();
        byte[] bc = PdfEncodings.convertToBytes(contents.getValue(), null);
        if (PdfName.ETSI_RFC3161.equals(sig.getSubFilter())) {
            try (   ASN1InputStream din = new ASN1InputStream(new ByteArrayInputStream(bc)) ) {
                ASN1Primitive pkcs = din.readObject();
                bc = pkcs.getEncoded();
            }
        }
        byte[] bt = hashBytesSha1(bc);
        return new PdfName(convertToHex(bt));
    }

    static byte[] hashBytesSha1(byte[] b) throws NoSuchAlgorithmException {
        MessageDigest sh = MessageDigest.getInstance("SHA1");
        return sh.digest(b);
    }

    static String convertToHex(byte[] bytes) {
        ByteBuffer buf = new ByteBuffer();
        for (byte b : bytes) {
            buf.appendHex(b);
        }
        return PdfEncodings.convertToString(buf.toByteArray(), null).toUpperCase();
    }

    //
    // OCSP response helpers
    //
    static X509Certificate getOcspSignerCertificate(byte[] basicResponseBytes) throws CertificateException, OCSPException, OperatorCreationException {
        JcaX509CertificateConverter converter = new JcaX509CertificateConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME);
        BasicOCSPResponse borRaw = BasicOCSPResponse.getInstance(basicResponseBytes);
        BasicOCSPResp bor = new BasicOCSPResp(borRaw);

        for (final X509CertificateHolder x509CertificateHolder : bor.getCerts()) {
            X509Certificate x509Certificate = converter.getCertificate(x509CertificateHolder);

            JcaContentVerifierProviderBuilder jcaContentVerifierProviderBuilder = new JcaContentVerifierProviderBuilder();
            jcaContentVerifierProviderBuilder.setProvider(BouncyCastleProvider.PROVIDER_NAME);
            final PublicKey publicKey = x509Certificate.getPublicKey();
            ContentVerifierProvider contentVerifierProvider = jcaContentVerifierProviderBuilder.build(publicKey);

            if (bor.isSignatureValid(contentVerifierProvider))
                return x509Certificate;
        }
        
        return null;
    }

    static byte[] buildOCSPResponse(byte[] BasicOCSPResponse) throws IOException {
        DEROctetString doctet = new DEROctetString(BasicOCSPResponse);
        ASN1EncodableVector v2 = new ASN1EncodableVector();
        v2.add(OCSPObjectIdentifiers.id_pkix_ocsp_basic);
        v2.add(doctet);
        ASN1Enumerated den = new ASN1Enumerated(0);
        ASN1EncodableVector v3 = new ASN1EncodableVector();
        v3.add(den);
        v3.add(new DERTaggedObject(true, 0, new DERSequence(v2)));            
        DERSequence seq = new DERSequence(v3);
        return seq.getEncoded();
    }

    //
    // X509 certificate related helpers
    //
    static X509Certificate getIssuerCertificate(X509Certificate certificate) throws IOException, StreamParsingException {
        String url = getCACURL(certificate);
        if (url != null && url.length() > 0) {
            HttpURLConnection con = (HttpURLConnection)new URL(url).openConnection();
            if (con.getResponseCode() / 100 != 2) {
                throw new PdfException(SignExceptionMessageConstant.INVALID_HTTP_RESPONSE).setMessageParams(con.getResponseCode());
            }
            InputStream inp = (InputStream) con.getContent();
            X509CertParser parser = new X509CertParser();
            parser.engineInit(new ByteArrayInputStream(StreamUtil.inputStreamToArray(inp)));
            return (X509Certificate) parser.engineRead();
        }
        return null;
    }

    static String getCACURL(X509Certificate certificate) {
        ASN1Primitive obj;
        try {
            obj = getExtensionValue(certificate, Extension.authorityInfoAccess.getId());
            if (obj == null) {
                return null;
            }
            ASN1Sequence AccessDescriptions = (ASN1Sequence) obj;
            for (int i = 0; i < AccessDescriptions.size(); i++) {
                ASN1Sequence AccessDescription = (ASN1Sequence) AccessDescriptions.getObjectAt(i);
                if ( AccessDescription.size() != 2 ) {
                    continue;
                }
                else if (AccessDescription.getObjectAt(0) instanceof ASN1ObjectIdentifier) {
                    ASN1ObjectIdentifier id = (ASN1ObjectIdentifier)AccessDescription.getObjectAt(0);
                    if ("1.3.6.1.5.5.7.48.2".equals(id.getId())) {
                        ASN1Primitive description = (ASN1Primitive)AccessDescription.getObjectAt(1);
                        String AccessLocation =  getStringFromGeneralName(description);
                        if (AccessLocation == null) {
                            return "" ;
                        }
                        else {
                            return AccessLocation ;
                        }
                    }
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    static ASN1Primitive getExtensionValue(X509Certificate certificate, String oid) throws IOException {
        byte[] bytes = certificate.getExtensionValue(oid);
        if (bytes == null) {
            return null;
        }
        ASN1InputStream aIn = new ASN1InputStream(new ByteArrayInputStream(bytes));
        ASN1OctetString octs = (ASN1OctetString) aIn.readObject();
        aIn = new ASN1InputStream(new ByteArrayInputStream(octs.getOctets()));
        return aIn.readObject();
    }

    private static String getStringFromGeneralName(ASN1Primitive names) throws IOException {
        ASN1TaggedObject taggedObject = (ASN1TaggedObject) names ;
        return new String(ASN1OctetString.getInstance(taggedObject, false).getOctets(), "ISO-8859-1");
    }

    //
    // inner class
    //
    static class ValidationData {
        final List<byte[]> crls = new ArrayList<byte[]>();
        final List<byte[]> ocsps = new ArrayList<byte[]>();
        final List<byte[]> certs = new ArrayList<byte[]>();
    }

    //
    // member variables
    //
    final PdfDocument pdfDocument;

    final Map<PdfName,ValidationData> validated = new HashMap<PdfName,ValidationData>();
}
