package mkl.testarea.itext7.merge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PageRange;
import com.itextpdf.kernel.utils.PdfSplitter;

/**
 * @author mkl
 */
public class SplitDocument {
    final static File RESULT_FOLDER = new File("target/test-outputs", "merge");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/50470662/error-about-indirect-object-with-some-pdf-when-splitting-merging">
     * Error about indirect object with some PDF when splitting / merging
     * </a>
     * <p>
     * This issue has been fixed in iText 7.1.3. More exactly, it has
     * been fixed in commit 251606e55768a47cb68eb8c58f2f5fe36324d85b
     * dated 2018-04-23 13:46:25 in the course of resolving issue
     * DEVSIX-1913 (Fix copying of inherited page entries).
     * </p>
     * <p>
     * The example PDF is confidential, so it's copied from outside the
     * project.
     * </p>
     */
    @Test
    public void testSplitLikeKGeorges() throws IOException {
        File sensitiveSource = new File("d:\\Issues\\stackoverflow\\Error about indirect object with some PDF when splitting  merging\\Crystal_Printout_551628_163115.pdf");
        File testCopy = new File(RESULT_FOLDER, "Crystal_Printout_551628_163115.pdf");
        Files.copy(sensitiveSource.toPath(), testCopy.toPath(), StandardCopyOption.REPLACE_EXISTING);

        splitByPage(testCopy, 100);
    }

    /** @see #testSplitLikeKGeorges() */
    public static void splitByPage(File pdfToSplit, int nbPageByPDF) throws IOException {
        // Open the document in reading mode
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdfToSplit));

        List<PdfDocument> splitDocuments = new PdfSplitter(pdfDoc) {
            int partNumber = 1;

            @Override
            protected PdfWriter getNextPdfWriter(PageRange documentPageRange) {
                try {
                    return new PdfWriter(pdfToSplit.getAbsolutePath()
                                                   .substring(0,
                                                              pdfToSplit.getAbsolutePath()
                                                                        .lastIndexOf(".")
                                                              ) 
                                            + "splitPage_part" 
                                            + String.valueOf(partNumber++) 
                                            + ".pdf");
                } catch (FileNotFoundException e) {
                    throw new RuntimeException();
                }
            }
        }.splitByPageCount(nbPageByPDF);

        // Close all the part created
        for (PdfDocument doc : splitDocuments) {
            doc.close(); // exception throws at the first closing
        }

        // Close the initial pdf to split
        pdfDoc.close();
    }
}
