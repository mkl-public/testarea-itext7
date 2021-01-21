package mkl.testarea.itext7.meta;

import java.io.IOException;
import java.util.List;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.SignatureUtil;

/**
 * This is a simple tool to compare revisions (signed and full file only)
 * of a PDF.
 * 
 * @author mkl
 */
public class PdfRevisionCompare extends PdfCompare {
    public static void main(String[] args) throws IOException {
        for (String arg : args) {
            System.out.printf("\nComparing revisions of: %s\n***********************\n", args[0]);
            try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(arg))) {
                SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);
                List<String> signatureNames = signatureUtil.getSignatureNames();
                if (signatureNames.isEmpty()) {
                    System.out.println("No signed revisions detected. (no AcroForm)");
                    continue;
                }
                String previousRevision = signatureNames.get(0);
                PdfDocument previousDocument = new PdfDocument(new PdfReader(signatureUtil.extractRevision(previousRevision)));
                System.out.printf("* Initial signed revision: %s\n", previousRevision);
                for (int i = 1; i < signatureNames.size(); i++) {
                    String currentRevision = signatureNames.get(i);
                    PdfDocument currentDocument = new PdfDocument(new PdfReader(signatureUtil.extractRevision(currentRevision)));
                    showDifferences(previousDocument, currentDocument);
                    System.out.printf("* Next signed revision (%d): %s\n", i+1, currentRevision);
                    previousDocument.close();
                    previousDocument = currentDocument;
                    previousRevision = currentRevision;
                }
                if (signatureUtil.signatureCoversWholeDocument(previousRevision)) {
                    System.out.println("No unsigned updates.");
                } else {
                    showDifferences(previousDocument, pdfDocument);
                    System.out.println("* Final unsigned revision");
                }
                previousDocument.close();
            }
        }
    }

    static void showDifferences(PdfDocument previousDocument, PdfDocument currentDocument) {
        PdfRevisionCompare pdfRevisionCompare = new PdfRevisionCompare(previousDocument, currentDocument);
        pdfRevisionCompare.compare();
        List<Difference> differences = pdfRevisionCompare.getDifferences();
        if (differences == null || differences.isEmpty()) {
            System.out.println("No differences found.");
        } else {
            System.out.printf("%d differences found:\n", differences.size());
            for (Difference difference : differences) {
                for (String element : difference.getPath()) {
                    System.out.print(element);
                }
                System.out.printf(" - %s\n", difference.getDescription());
            }
        }
    }

    public PdfRevisionCompare(PdfDocument pdfDocument1, PdfDocument pdfDocument2) {
        super(pdfDocument1, pdfDocument2);
    }
}
