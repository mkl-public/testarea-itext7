package mkl.testarea.itext7.annotate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;

/**
 * @author mkl
 */
public class RemoveLinkAnnotations {
    final static File RESULT_FOLDER = new File("target/test-outputs", "annotate");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/50477506/remove-links-from-a-pdf-using-itext-7-1">
     * Remove links from a PDF using iText 7.1
     * </a>
     * <br/>
     * <a href="https://drive.google.com/open?id=1_6HwkN1svVmBuhM7tpXXEkB0MMXtTGQv">
     * test-with-links.pdf
     * </a>
     * <p>
     * This code by the OP removes all annotations.
     * </p>
     */
    @Test
    public void testRemoveLinksByClass() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("test-with-links.pdf")) {
            PdfReader reader = new PdfReader(resource);
            PdfWriter writer = new PdfWriter(new File(RESULT_FOLDER, "test-no-links.pdf"));
            PdfDocument pdfDoc = new PdfDocument(reader, writer);

            for (int page = 1; page <= pdfDoc.getNumberOfPages(); ++page) {
                PdfPage pdfPage = pdfDoc.getPage(page);
                List<PdfAnnotation> annots = pdfPage.getAnnotations();

                if ((annots == null) || (annots.size() == 0)) {
                    System.out.println("no annotations on page " + page);
                } else {
                    for (PdfAnnotation annot : annots) {
                        if (annot instanceof PdfLinkAnnotation) {
                            pdfPage.removeAnnotation(annot);
                        }
                    }
                }
            }
            pdfDoc.close();
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/50477506/remove-links-from-a-pdf-using-itext-7-1">
     * Remove links from a PDF using iText 7.1
     * </a>
     * <br/>
     * <a href="https://drive.google.com/open?id=1_6HwkN1svVmBuhM7tpXXEkB0MMXtTGQv">
     * test-with-links.pdf
     * </a>
     * <p>
     * This code by the OP removes all Link annotations.
     * </p>
     */
    @Test
    public void testRemoveLinksBySubclass() throws IOException {
        try (InputStream resource = getClass().getResourceAsStream("test-with-links.pdf")) {
            PdfReader reader = new PdfReader(resource);
            PdfWriter writer = new PdfWriter(new File(RESULT_FOLDER, "test-no-links-1.pdf"));

            PdfDocument pdfDoc = new PdfDocument(reader, writer);

            for (int page = 1; page <= pdfDoc.getNumberOfPages(); ++page) {
                PdfPage pdfPage = pdfDoc.getPage(page);
                List<PdfAnnotation> annots = pdfPage.getAnnotations();

                if ((annots == null) || (annots.size() == 0)) {
                    System.out.println("no annotations on page " + page);
                } else {
                    for (PdfAnnotation annot : annots) {
                        // if this annotation has a link, delete it
                        if (annot.getSubtype().equals(PdfName.Link)) {
                            PdfDictionary annotAction = ((PdfLinkAnnotation) annot).getAction();

                            if (annotAction != null) {
                                if (annotAction.get(PdfName.S).equals(PdfName.URI)
                                        || annotAction.get(PdfName.S).equals(PdfName.GoToR)) {
                                    PdfString uri = annotAction.getAsString(PdfName.URI);
                                    System.out.println("Removing " + uri.toString());
                                    pdfPage.removeAnnotation(annot);
                                }
                            }
                        }
                    }
                }
            }
            pdfDoc.close();
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/50477506/remove-links-from-a-pdf-using-itext-7-1">
     * Remove links from a PDF using iText 7.1
     * </a>
     * <br/>
     * <a href="https://drive.google.com/open?id=1_6HwkN1svVmBuhM7tpXXEkB0MMXtTGQv">
     * test-with-links.pdf
     * </a>
     * <p>
     * This code by the OP removes all annotations.
     * </p>
     */
    @Test
    public void testRemoveAllAnnotations() throws IOException {
        try (InputStream resource = getClass().getResourceAsStream("test-with-links.pdf")) {
            PdfReader reader = new PdfReader(resource);
            PdfWriter writer = new PdfWriter(new File(RESULT_FOLDER, "test-no-links-2.pdf"));

            PdfDocument pdfDoc = new PdfDocument(reader, writer);

            for (int page = 1; page <= pdfDoc.getNumberOfPages(); ++page) {
                PdfPage pdfPage = pdfDoc.getPage(page);

                // remove all annotations from the page regardless of type
                pdfPage.getPdfObject().remove(PdfName.Annots);
            }
            pdfDoc.close();
        }
    }
}
