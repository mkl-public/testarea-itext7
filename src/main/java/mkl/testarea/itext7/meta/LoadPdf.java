package mkl.testarea.itext7.meta;

import java.io.IOException;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;

/**
 * @author mkl
 */
public class LoadPdf {

    public static void main(String[] args) throws IOException {
        for (String arg : args) {
            System.out.printf("Loading: %s\n", arg);

            try (   PdfReader pdfReader = new PdfReader(arg);
                    PdfDocument pdfDocument = new PdfDocument(pdfReader)    ) {
                System.out.printf("Finished loading\n");
            }
        }
    }

}
