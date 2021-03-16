package mkl.testarea.itext7.cleanup;

import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfTextArray;

/**
 * @author mkl
 */
public class CancelingAdjustments {

    /**
     * <a href="https://stackoverflow.com/questions/66561817/itext7-cleanup-method-throws-error-index-was-out-of-range">
     * Itext7 cleanup method throws error - Index was out of range
     * </a>
     * <p>
     * This test reproduces a stack trace like the one posted by the OP.
     * </p>
     */
    @Test
    public void testCancelingAdjustments() {
        PdfTextArray textArray = new PdfTextArray();
        textArray.add(1);
        textArray.add(-1);
        textArray.add(1);
    }

}
