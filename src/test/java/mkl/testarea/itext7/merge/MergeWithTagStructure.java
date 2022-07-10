package mkl.testarea.itext7.merge;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;

/**
 * @author mkl
 */
public class MergeWithTagStructure {
    final static File RESULT_FOLDER = new File("target/test-outputs", "merge");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/72922255/when-merging-pdfs-on-certain-order-getting-tag-structure-copying-failed">
     * When merging pdfs on certain order getting "Tag structure copying failed"
     * </a>
     * <br/>
     * <a href="https://drive.google.com/file/d/1ELt-bPKFNJgUzvuu55c8sjUT3hI8fios/view?usp=sharing">
     * 10840.pdf,
     * invoice.pdf,
     * B.Tech-Cyber-180-Credits.docx.pdf
     * </a>
     * <p>
     * This is the OP's code applied to his good case.
     * </p>
     * @see #testMergeLikeDeepanshuBad()}
     * @see #testMergeLikeDeepanshuButUnTaggedGood()
     * @see #testMergeLikeDeepanshuButUnTaggedBad()
     */
    @Test
    public void testMergeLikeDeepanshuGood() throws IOException {
        File[] filesToMerge = new File[] {
                new File("src/test/resources/mkl/testarea/itext7/merge/B.Tech-Cyber-180-Credits.docx.pdf"),
                new File("src/test/resources/mkl/testarea/itext7/merge/invoice.pdf"),
                new File("src/test/resources/mkl/testarea/itext7/merge/10840.pdf") };
        mergeLikeDeepanshu(new File(RESULT_FOLDER, "MergeLikeDeepanshuGood.pdf"), filesToMerge);
    }

    /**
     * <p>
     * This is the OP's code applied to his bad case.
     * </p>
     * @see #testMergeLikeDeepanshuGood()}
     * @see #testMergeLikeDeepanshuButUnTaggedGood()
     * @see #testMergeLikeDeepanshuButUnTaggedBad()
     */
    @Test
    public void testMergeLikeDeepanshuBad() throws IOException {
        File[] filesToMerge = new File[] {
                new File("src/test/resources/mkl/testarea/itext7/merge/10840.pdf"),
                new File("src/test/resources/mkl/testarea/itext7/merge/invoice.pdf"),
                new File("src/test/resources/mkl/testarea/itext7/merge/B.Tech-Cyber-180-Credits.docx.pdf") };
        mergeLikeDeepanshu(new File(RESULT_FOLDER, "MergeLikeDeepanshuBad.pdf"), filesToMerge);
    }

    /**
     * <p>
     * This is the OP's merging code.
     * </p>
     * <p>
     * If the first file in the array is un-tagged, the tagging information
     * of the first merged file with tagging information is flushed out which
     * causes issues when the next tagged file is merged.
     * </p>
     * 
     * @see #testMergeLikeDeepanshuGood()
     * @see #testMergeLikeDeepanshuBad()
     */
    void mergeLikeDeepanshu(File destinationFile, File[] filesToMergeList) throws IOException {
        File firstFileInMergeList = filesToMergeList[0];

        PdfReader pdfReader = new PdfReader(firstFileInMergeList);

        PdfWriter pdfWriter = new PdfWriter(destinationFile);

        PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter);

        PdfMerger merger = new PdfMerger(pdfDocument);

        for (int index = 1; index < filesToMergeList.length; index++) {
            PdfDocument pdfDocument2 = new PdfDocument(new PdfReader(filesToMergeList[index]));

            merger.merge(pdfDocument2, 1, pdfDocument2.getNumberOfPages());

            pdfDocument.flushCopiedObjects(pdfDocument2);

            pdfDocument2.close();
        }

        pdfDocument.close();
    }

    /**
     * <p>
     * This is the OP's code modified to merge without tagging applied to his good case.
     * </p>
     * @see #testMergeLikeDeepanshuGood()}
     * @see #testMergeLikeDeepanshuBad()
     * @see #testMergeLikeDeepanshuButUnTaggedBad()
     */
    @Test
    public void testMergeLikeDeepanshuButUnTaggedGood() throws IOException {
        File[] filesToMerge = new File[] {
                new File("src/test/resources/mkl/testarea/itext7/merge/B.Tech-Cyber-180-Credits.docx.pdf"),
                new File("src/test/resources/mkl/testarea/itext7/merge/invoice.pdf"),
                new File("src/test/resources/mkl/testarea/itext7/merge/10840.pdf") };
        mergeLikeDeepanshuButUnTagged(new File(RESULT_FOLDER, "MergeLikeDeepanshuButUnTaggedGood.pdf"), filesToMerge);
    }

    /**
     * <p>
     * This is the OP's code modified to merge without tagging applied to his bad case.
     * </p>
     * @see #testMergeLikeDeepanshuGood()}
     * @see #testMergeLikeDeepanshuBad()
     * @see #testMergeLikeDeepanshuButUnTaggedGood()
     */
    @Test
    public void testMergeLikeDeepanshuButUnTaggedBad() throws IOException {
        File[] filesToMerge = new File[] { new File("src/test/resources/mkl/testarea/itext7/merge/10840.pdf"),
                new File("src/test/resources/mkl/testarea/itext7/merge/invoice.pdf"),
                new File("src/test/resources/mkl/testarea/itext7/merge/B.Tech-Cyber-180-Credits.docx.pdf") };
        mergeLikeDeepanshuButUnTagged(new File(RESULT_FOLDER, "MergeLikeDeepanshuButUnTaggedBad.pdf"), filesToMerge);
    }

    /**
     * <p>
     * This is the OP's merging code modified to merge with consideration of tagging information.
     * </p>
     * <p>
     * Here it doesn't matter anymore if some tagging information is being flushed during the merge.
     * </p>
     * 
     * @see #testMergeLikeDeepanshuButUnTaggedGood()
     * @see #testMergeLikeDeepanshuButUnTaggedBad()
     */
    void mergeLikeDeepanshuButUnTagged(File destinationFile, File[] filesToMergeList) throws IOException {
        File firstFileInMergeList = filesToMergeList[0];

        PdfReader pdfReader = new PdfReader(firstFileInMergeList);

        PdfWriter pdfWriter = new PdfWriter(destinationFile);

        PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter);

        PdfMerger merger = new PdfMerger(pdfDocument, false, true);

        for (int index = 1; index < filesToMergeList.length; index++) {
            PdfDocument pdfDocument2 = new PdfDocument(new PdfReader(filesToMergeList[index]));

            merger.merge(pdfDocument2, 1, pdfDocument2.getNumberOfPages());

            pdfDocument.flushCopiedObjects(pdfDocument2);

            pdfDocument2.close();
        }

        pdfDocument.close();
    }

}
