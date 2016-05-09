// $Id$
package mkl.testarea.itext7.signature;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.security.cert.X509Certificate;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.signatures.CertificateInfo;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.SignatureUtil;

/**
 * @author mkl
 */
public class ChangeSignatureAppearance
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
     * <a href="http://stackoverflow.com/questions/37027579/how-to-associate-a-previous-signature-in-a-new-signature-field">
     * How to associate a previous signature in a new signature field
     * </a>
     * <br/>
     * <span>BLANK-signed.pdf, <em>a blank file from elsewhere with an invisible signature.</em></span>
     * <p>
     * Quite surprisingly it turns out that changing the signature appearance is possible without
     * breaking the signature, merely a warning appears which can be hidden by simply signing again.
     * </p>
     */
    @Test
    public void testChangeAppearances() throws IOException
    {
        try (   InputStream resource = getClass().getResourceAsStream("BLANK-signed.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "BLANK-signed-app.pdf"));
                PdfWriter pdfWriter = new PdfWriter(result);
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter, new StampingProperties().useAppendMode()))
        {
            SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);
            PdfAcroForm acroForm = PdfAcroForm.getAcroForm(pdfDocument, false);

            for (String name : signatureUtil.getSignatureNames())
            {
                PdfFormField field = acroForm.getField(name);
                field.setModified();
                for (PdfWidgetAnnotation pdfWidgetAnnotation : field.getWidgets())
                {
                    pdfWidgetAnnotation.setRectangle(new PdfArray(new int[]{100, 100, 200, 200}));

                    PdfFormXObject form = new PdfFormXObject(new Rectangle(100, 100));
                    PdfCanvas canvas = new PdfCanvas(form, pdfDocument);
                    canvas.setStrokeColor(Color.RED);
                    canvas.moveTo(0, 0);
                    canvas.lineTo(99, 99);
                    canvas.moveTo(0, 99);
                    canvas.lineTo(99, 0);
                    canvas.stroke();

                    pdfWidgetAnnotation.setNormalAppearance(form.getPdfObject());
                }
            }
        }
    }

    /**
     * <a href="http://stackoverflow.com/questions/37027579/how-to-associate-a-previous-signature-in-a-new-signature-field">
     * How to associate a previous signature in a new signature field
     * </a>
     * <br/>
     * <span>BLANK-signed.pdf, <em>a blank file from elsewhere with an invisible signature.</em></span>
     * <p>
     * Similarly to {@link #testChangeAppearances()}, this test adds a signature appearance
     * not breaking signature validity, but this time it contains the signer certificate
     * subject common name.
     * </p>
     */
    @Test
    public void testChangeAppearancesWithName() throws IOException
    {
        try (   InputStream resource = getClass().getResourceAsStream("BLANK-signed.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "BLANK-signed-app-name.pdf"));
                PdfWriter pdfWriter = new PdfWriter(result);
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter, new StampingProperties().useAppendMode()))
        {
            SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);
            PdfAcroForm acroForm = PdfAcroForm.getAcroForm(pdfDocument, false);

            for (String name : signatureUtil.getSignatureNames())
            {
                PdfPKCS7 pkcs7 = signatureUtil.verifySignature(name);
                X509Certificate signerCert = (X509Certificate) pkcs7.getSigningCertificate();
                String signerName = CertificateInfo.getSubjectFields(signerCert).getField("CN");
                PdfFormField field = acroForm.getField(name);
                field.setModified();
                for (PdfWidgetAnnotation pdfWidgetAnnotation : field.getWidgets())
                {
                    pdfWidgetAnnotation.setRectangle(new PdfArray(new int[]{100, 100, 200, 200}));

                    PdfFormXObject form = new PdfFormXObject(new Rectangle(100, 100));
                    Canvas canvas = new Canvas(form, pdfDocument);
                    canvas.add(new Paragraph().setItalic().add("Signed by:"));
                    canvas.add(new Paragraph().setBold().add(signerName));

                    pdfWidgetAnnotation.setNormalAppearance(form.getPdfObject());
                }
            }
        }
    }
}
