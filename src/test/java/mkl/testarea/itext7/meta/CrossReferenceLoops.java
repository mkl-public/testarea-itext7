package mkl.testarea.itext7.meta;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;

/**
 * @author mkl
 */
public class CrossReferenceLoops {
    /**
     * Attempts to open a file with a single cross reference stream
     * whose Prev entry points back to itself.
     */
    @Test
    public void testSingleXrefStream() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("loopInXref-stream-single.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfDocument pdfDocument = new PdfDocument(pdfReader)) {
            System.out.printf("Cross references are fixed? %s\n", pdfReader.hasFixedXref());
            System.out.printf("Cross references are re-built? %s\n", pdfReader.hasRebuiltXref());
        }
    }

    /**
     * Attempts to open a file with two cross reference streams
     * whose Prev entries point to each other.
     */
    @Test
    public void testDoubleXrefStream() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("loopInXref-stream-double.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfDocument pdfDocument = new PdfDocument(pdfReader)) {
            System.out.printf("Cross references are fixed? %s\n", pdfReader.hasFixedXref());
            System.out.printf("Cross references are re-built? %s\n", pdfReader.hasRebuiltXref());
        }
    }

    /**
     * Attempts to open a file with a single cross reference table
     * whose Prev entry points back to itself.
     */
    @Test
    public void testSingleXrefTable() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("loopInXref-table-single.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfDocument pdfDocument = new PdfDocument(pdfReader)) {
            System.out.printf("Cross references are fixed? %s\n", pdfReader.hasFixedXref());
            System.out.printf("Cross references are re-built? %s\n", pdfReader.hasRebuiltXref());
        }
    }

    /**
     * Attempts to open a file with two cross reference tables
     * whose Prev entries point to each other.
     */
    @Test
    public void testDoubleXrefTables() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("loopInXref-table-double.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfDocument pdfDocument = new PdfDocument(pdfReader)) {
            System.out.printf("Cross references are fixed? %s\n", pdfReader.hasFixedXref());
            System.out.printf("Cross references are re-built? %s\n", pdfReader.hasRebuiltXref());
        }
    }

}
