package mkl.testarea.itext7.meta;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNameTree;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;

/**
 * @author mkl
 */
public class ShowDocumentLevelJavaScript {
    /**
     * <a href="https://stackoverflow.com/questions/48445694/itext7-equivalent-method-for-getjavascript-in-pdfreader-class-in-itext7">
     * itext7 - equivalent method for getJavascript in pdfReader class in itext7
     * </a>
     * <p>
     * This test shows how to export all document level JavaScript. It
     * essentially does the same as the iText 5 <code>PdfReader</code>
     * method <code>getJavaScript</code>.
     * </p>
     */
    @Test
    public void testAppearance_TestingTest854729() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("Appearance_Testing Test854729.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfDocument pdfDocument = new PdfDocument(pdfReader)) {
            PdfNameTree javascript = pdfDocument.getCatalog().getNameTree(PdfName.JavaScript);
            Map<String, PdfObject> objs2 = javascript.getNames();
            for (Map.Entry<String, PdfObject> entry : objs2.entrySet())
            {
                System.out.println();
                System.out.println(entry.getKey());
                System.out.println();

                PdfObject object = entry.getValue();
                if (object.isDictionary()) {
                    object = ((PdfDictionary)object).get(PdfName.JS);
                    if (object.isString()) {
                        System.out.println(((PdfString)object).getValue());
                    } else if (object.isStream()) {
                        System.out.println(new String(((PdfStream)object).getBytes()));
                    }
                }
                
                System.out.println();
            }
        }
    }

}
