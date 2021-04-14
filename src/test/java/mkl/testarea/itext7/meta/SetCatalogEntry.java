package mkl.testarea.itext7.meta;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfCatalog;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;

public class SetCatalogEntry {
    final static File RESULT_FOLDER = new File("target/test-outputs", "meta");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/67093875/using-itext-java-to-add-a-new-entry-to-the-pdf-catalog">
     * Using iText Java to add a new entry to the PDF Catalog
     * </a>
     * <p>
     * In contrast to what the OP said, the entry was there. But
     * he probably expected /MyVar instead of /#2fMyVar and should
     * therefore use <code>new PdfName("MyVar")</code> instead of
     * <code>new PdfName("/MyVar")</code>.
     * </p>
     */
    @Test
    public void testSetLikeFelipe() throws IOException {
        String path = new File(RESULT_FOLDER, "CatalogEntryLikeFelipe.pdf").getAbsolutePath();
        
        PdfWriter pdfWriter = new PdfWriter(path);
        
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        
        pdfDocument.addNewPage();
        
        int dia = 14;
        
        PdfNumber value = new PdfNumber(dia);
        
        PdfObject obj;
        
        obj = value;
        
        PdfName key = new PdfName("/MyVar");
        
        PdfCatalog cat = pdfDocument.getCatalog();
        
        cat.put(key, value);        

        Document document = new Document(pdfDocument);
        
        document.close();
    }

}
