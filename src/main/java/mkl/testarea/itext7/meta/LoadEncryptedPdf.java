package mkl.testarea.itext7.meta;

import java.io.IOException;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.ReaderProperties;

/**
 * @author mkl
 */
public class LoadEncryptedPdf {

    public static void main(String[] args) throws IOException {
        for (int i = 0; i < args.length - 1; i+=2) {
            String arg = args[i];
            String password = args[i + 1];
            System.out.printf("Loading: %s with %s\n", arg, password);

            try (   PdfReader pdfReader = new PdfReader(arg, new ReaderProperties().setPassword(password.getBytes()));
                    PdfDocument pdfDocument = new PdfDocument(pdfReader)    ) {
                System.out.printf("Finished loading\n");
            }
        }
    }

}
