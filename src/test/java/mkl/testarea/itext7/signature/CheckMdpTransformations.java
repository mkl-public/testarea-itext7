package mkl.testarea.itext7.signature;

import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.SignatureUtil;

/**
 * @author mkl
 */
public class CheckMdpTransformations {

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * <a href="https://stackoverflow.com/questions/58397100/itext7-c-sharp-check-pdf-was-locked-after-signature">
     * iText7 C# Check PDF was locked after signature
     * </a>
     * <p>
     * This test illustrates how to output the MDP information
     * of signatures of a PDF.
     * </p>
     */
    @Test
    public void testShowMdpForStep4SignedByAliceBobCarolAndDave() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("step_4_signed_by_alice_bob_carol_and_dave.pdf");
                PdfReader reader = new PdfReader(resource);
                PdfDocument document = new PdfDocument(reader)) {
            SignatureUtil signatureUtil = new SignatureUtil(document);
            for (String name : signatureUtil.getSignatureNames()) {
                System.out.printf("\nInspecting signature '%s':\n", name);
                PdfDictionary dict = signatureUtil.getSignatureDictionary(name);

                PdfArray referenceArray = dict.getAsArray(PdfName.Reference);
                if (referenceArray == null | referenceArray.size() == 0) {
                    System.out.println("The signature does not apply a transform.");
                    continue;
                }

                for (PdfObject referenceObject : referenceArray) {
                    if (referenceObject.isIndirectReference())
                        referenceObject = ((PdfIndirectReference)referenceObject).getRefersTo(true);
                    if (referenceObject.isIndirectReference()) {
                        System.out.println("A transform is too deeply nested.");
                        continue;
                    }
                    if (!referenceObject.isDictionary()) {
                        System.out.println("A transform is not a dictionary.");
                        continue;
                    }
                    PdfDictionary reference = (PdfDictionary) referenceObject;

                    PdfName method = reference.getAsName(PdfName.TransformMethod);
                    if (method == null) {
                        System.out.println("The signature does not provide the name of its transform method. (Invalid!)");
                        continue;
                    } 
                    if (new PdfName("UR").equals(method)) {
                        System.out.println("The signature does not apply MDP but is a usage rights signature.");
                        continue;
                    } 
                    if (PdfName.DocMDP.equals(method)) {
                        System.out.println("The signature has a DocMDP transform method, it is a certification signature.");
                    } else if (PdfName.FieldMDP.equals(method)) {
                        System.out.println("The signature has a FieldMDP transform method.");
                    } else {
                        System.out.printf("The signature has the unknown '%s' transform method. (Invalid!)\n", method);
                        continue;
                    }

                    PdfDictionary transformParams = reference.getAsDictionary(PdfName.TransformParams);
                    if (transformParams == null) {
                        System.out.println("The transform has no parameters. (Invalid!)");
                        continue;
                    }

                    PdfName action = transformParams.getAsName(PdfName.Action);
                    if (action != null) {
                        if (PdfName.All.equals(action)) {
                            System.out.println("The transform locks all form fields.");
                        } else {
                            PdfArray fields = transformParams.getAsArray(PdfName.Fields);
                            if (PdfName.Include.equals(action)) {
                                if (fields == null)
                                    System.out.println("The transform locks all listed form fields but does not provide the list. (Invalid!)");
                                else
                                    System.out.printf("The transform locks all the listed form fields: %s\n", fields);
                            } else if (PdfName.Exclude.equals(action)) {
                                if (fields == null)
                                    System.out.println("The transform locks all except listed form fields but does not provide the list. (Invalid!)");
                                else
                                    System.out.printf("The transform locks all except the listed form fields: %s\n", fields);
                            } else {
                                System.out.printf("The transform uses the unknown action '%s' for field locking. (Invalid!)\n", action);
                            }
                        }
                    }

                    PdfNumber p = transformParams.getAsNumber(PdfName.P);
                    if (p != null) {
                        switch (p.intValue()) {
                        case 1:
                            System.out.println("The transform locks the document entirely.");
                            break;
                        case 2:
                            System.out.println("The transform restricts document manipulation to at most filling in forms, instantiating page templates, and signing.");
                            break;
                        case 3:
                            System.out.println("The transform restricts document manipulation to at most filling in forms, instantiating page templates, and signing, as well as annotation creation, deletion, and modification.");
                            break;
                        default:
                            System.out.printf("The transform access permissions value is unknown: %s. (Invalid!)\n", p.intValue());
                            break;
                        }
                        System.out.println("In a PAdES or PDF-2 context, addition of validation related information and proofs of existence is additionally allowed.");
                    }
                }
            }
        }
    }

}
