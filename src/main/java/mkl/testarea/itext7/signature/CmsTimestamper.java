package mkl.testarea.itext7.signature;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;

import com.itextpdf.signatures.ITSAClient;

/**
 * <a href="https://stackoverflow.com/questions/68056676/add-inner-timestamp-after-the-signing">
 * Add signature timestamp after the signing
 * </a>
 * <p>
 * This class shows how to add a signature timestamp to an existing
 * CMS signature container.
 * </p>
 * 
 * @author mkl
 */
public class CmsTimestamper {
    public CmsTimestamper(ITSAClient tsaClient) {
        this.tsaClient = tsaClient;
    }

    public CMSSignedData timestamp(CMSSignedData signedData) throws Exception {
        List<SignerInformation> newSignerInformations = new ArrayList<>();
        for (SignerInformation signerInformation : signedData.getSignerInfos()) {
            AttributeTable unsignedAttributes = signerInformation.getUnsignedAttributes();
            if (unsignedAttributes == null) {
                unsignedAttributes = new AttributeTable(new Hashtable<>());
            } else if (unsignedAttributes.get(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken) != null) {
                System.out.println("   SignerInfo already has a time stamp; continuing with next SignerInfo.");
                newSignerInformations.add(signerInformation);
                continue;
            }

            byte[] signature = signerInformation.getSignature();
            byte[] signatureDigest = tsaClient.getMessageDigest().digest(signature);
            byte[] timestampToken = tsaClient.getTimeStampToken(signatureDigest);

            if (timestampToken == null) {
                System.out.println("   Failure restrieving timestamp token; continuing with next SignerInfo.");
                newSignerInformations.add(signerInformation);
                continue;
            }

            unsignedAttributes = unsignedAttributes.add(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken, ASN1Primitive.fromByteArray(timestampToken));
            SignerInformation newSignerInformation = SignerInformation.replaceUnsignedAttributes(signerInformation, unsignedAttributes);
            newSignerInformations.add(newSignerInformation);
        }

        final SignerInformationStore newSignerStore = new SignerInformationStore(newSignerInformations);
        return CMSSignedData.replaceSigners(signedData, newSignerStore);
    }

    final ITSAClient tsaClient;
}
