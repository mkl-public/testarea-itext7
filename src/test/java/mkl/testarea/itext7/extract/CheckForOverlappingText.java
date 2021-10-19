package mkl.testarea.itext7.extract;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.parser.PdfDocumentContentParser;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.FontKerning;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.licensekey.LicenseKey;

/**
 * @author mkl
 */
public class CheckForOverlappingText {
    final static File RESULT_FOLDER = new File("target/test-outputs", "extract");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/46362605/how-to-find-whether-a-pdf-file-has-overlapping-text-or-not-using-c-sharp">
     * How to find whether a PDF file has overlapping text or not, using c#
     * </a>
     * <p>
     * This method tests the {@link OverlappingTextSearchingStrategy}
     * against a document with very tightly set text. As expected there
     * are many false positives.
     * </p>
     */
    @Test
    public void testSampleCMYK() throws IOException {
        System.out.println("\nsampleCMYK.pdf\n==============");
        try (   InputStream resource = getClass().getResourceAsStream("sampleCMYK.pdf") ) {
            System.out.println("Page 1");
            boolean flag = checkForOverlappingText(resource, 1);
            System.out.printf("Found overlapping text? - %s\n", flag);
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/46362605/how-to-find-whether-a-pdf-file-has-overlapping-text-or-not-using-c-sharp">
     * How to find whether a PDF file has overlapping text or not, using c#
     * </a>
     * <p>
     * This method tests the {@link OverlappingTextSearchingStrategy}
     * against a document with loosely set text. As expected there
     * are no false positives.
     * </p>
     */
    @Test
    public void testInterinosDisponibles0590_20170915() throws IOException {
        System.out.println("\nInterinos disponibles 0590 20170915.pdf\n==============");
        try (   InputStream resource = getClass().getResourceAsStream("Interinos disponibles 0590 20170915.pdf") ) {
            System.out.println("Page 1");
            boolean flag = checkForOverlappingText(resource, 1);
            System.out.printf("Found overlapping text? - %s\n", flag);
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/46362605/how-to-find-whether-a-pdf-file-has-overlapping-text-or-not-using-c-sharp">
     * How to find whether a PDF file has overlapping text or not, using c#
     * </a>
     * <p>
     * This method tests the {@link OverlappingTextSearchingStrategy}
     * against a document with overlapped text. As expected the strategy
     * recognizes overlapped text.
     * </p>
     */
    @Test
    public void testOverlapTestPdf() throws IOException {
        System.out.println("\nOverlapTestPdf\n==============");
        try (   InputStream resource = new ByteArrayInputStream(createOverlapTestPdf()) ) {
            System.out.println("Page 1");
            boolean flag = checkForOverlappingText(resource, 1);
            System.out.printf("Found overlapping text? - %s\n", flag);
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/46362605/how-to-find-whether-a-pdf-file-has-overlapping-text-or-not-using-c-sharp">
     * How to find whether a PDF file has overlapping text or not, using c#
     * </a>
     * <p>
     * This method tests the {@link OverlappingTextSearchingStrategy}
     * against a document without overlapped text. As expected the
     * strategy recognizes no overlapped text.
     * </p>
     */
    @Test
    public void testNonOverlapTestPdf() throws IOException {
        System.out.println("\nNonOverlapTestPdf\n==============");
        try (   InputStream resource = new ByteArrayInputStream(createNonOverlapTestPdf()) ) {
            System.out.println("Page 1");
            boolean flag = checkForOverlappingText(resource, 1);
            System.out.printf("Found overlapping text? - %s\n", flag);
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/46362605/how-to-find-whether-a-pdf-file-has-overlapping-text-or-not-using-c-sharp">
     * How to find whether a PDF file has overlapping text or not, using c#
     * </a>
     * <p>
     * This method calls the {@link OverlappingTextSearchingStrategy}
     * which attempts to check whether overlapping text as defined by
     * the OP exists on the page parsed through it.
     * </p>
     */
    public boolean checkForOverlappingText(InputStream pdf, int page) throws IOException {
        PdfReader reader = new PdfReader(pdf);
        PdfDocument document = new PdfDocument(reader);
        PdfDocumentContentParser contentParser = new PdfDocumentContentParser(document);
        OverlappingTextSearchingStrategy strategy = contentParser.processContent(page, new OverlappingTextSearchingStrategy());
        return strategy.foundOverlappingText();
    }

    /**
     * <a href="https://stackoverflow.com/questions/46362605/how-to-find-whether-a-pdf-file-has-overlapping-text-or-not-using-c-sharp">
     * How to find whether a PDF file has overlapping text or not, using c#
     * </a>
     * <p>
     * This method creates a PDF with overlapping text.
     * </p>
     */
    byte[] createOverlapTestPdf() throws IOException
    {
        try (   ByteArrayOutputStream baos = new ByteArrayOutputStream()    ) {
            try (   PdfWriter pdfWriter = new PdfWriter(baos);
                    PdfDocument pdfDocument = new PdfDocument(pdfWriter);
                    Document document = new Document(pdfDocument)   ) {
                document.showTextAligned("Test part 1", 100, 100, TextAlignment.LEFT);
                document.showTextAligned("Test part 2", 107, 107, TextAlignment.LEFT);
            }
            return baos.toByteArray();
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/46362605/how-to-find-whether-a-pdf-file-has-overlapping-text-or-not-using-c-sharp">
     * How to find whether a PDF file has overlapping text or not, using c#
     * </a>
     * <p>
     * This method creates a PDF without overlapping text.
     * </p>
     */
    byte[] createNonOverlapTestPdf() throws IOException
    {
        LicenseKey.loadLicenseFile("itextkey-html2pdf_typography.xml");
        PdfFont font = PdfFontFactory.createFont("c:\\Windows\\Fonts\\arial.ttf");

        try (   ByteArrayOutputStream baos = new ByteArrayOutputStream()    ) {
            try (   PdfWriter pdfWriter = new PdfWriter(baos);
                    PdfDocument pdfDocument = new PdfDocument(pdfWriter);
                    Document document = new Document(pdfDocument)   ) {
                Paragraph paragraph = new Paragraph().setFont(font).add("A very testy test example with specials: MW, AW, AV")
                        .setMultipliedLeading(1).setMargin(0).setFontKerning(FontKerning.YES);
                document.showTextAligned(paragraph, 100, 100, TextAlignment.LEFT);
            }
            return baos.toByteArray();
        }
    }
}
