package mkl.testarea.itext7.content;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.font.FontProvider;

public class UseFontProvider {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/57139815/determine-dynamic-font-for-i18n-while-writing-pdf-using-itext">
     * Determine dynamic Font for i18n while writing pdf using iText
     * </a>
     * <br/>
     * <a href="https://www.google.com/get/noto">
     * NotoNaskhArabic-Regular.ttf, NotoSansThai-Regular.ttf,
     * NotoSansCJKkr-Regular.otf, NotoSansCJKsc-Regular.otf, NotoSansCJKtc-Regular.otf
     * </a>
     * <p>
     * This test demonstrates the {@link FontProvider} mechanism
     * applied to the OP's example string.
     * </p>
     */
    @Test
    public void testFontProviderForAshaKoshti() throws IOException {
        FontProvider provider = new FontProvider();
        for (String resourceName : new String[] { "NotoNaskhArabic-Regular.ttf", "NotoSansCJKkr-Regular.otf", "NotoSansCJKsc-Regular.otf", "NotoSansCJKtc-Regular.otf", "NotoSansThai-Regular.ttf " }) {
            try (   InputStream resource = getClass().getResourceAsStream(resourceName) ) {
                provider.addFont(StreamUtil.inputStreamToArray(resource));
            }
        }

        try (   PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new File(RESULT_FOLDER, "FontProviderForAshaKoshti.pdf")));
                Document doc = new Document(pdfDoc) ) {
            doc.setFontProvider(provider);
            doc.setFontFamily("NotoSans");

            Paragraph p = new Paragraph("Hello Everyone 是时候了 쓰기 รูปแบบไฟล์ PDF");
            doc.add(p);
        }

        try (   PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new File(RESULT_FOLDER, "FontProviderForAshaKoshti2.pdf")));
                Document doc = new Document(pdfDoc) ) {
            doc.setFontProvider(provider);
            doc.setFontFamily("NotoSans", "Noto Naskh Arabic");

            Paragraph p = new Paragraph("Hello Everyone 是时候了 쓰기 รูปแบบไฟล์ PDF");
            doc.add(p);
            doc.close();

            Assert.fail("Expected an exception.");
        } catch (PdfException ex) {
            System.out.printf("Expected exception %s: %s\n", PdfException.class.getName(), PdfException.PdfIndirectObjectBelongsToOtherPdfDocument);
            System.out.printf("Caught exception %s: %s\n", ex.getClass().getName(), ex.getMessage());
        }
    }

}
