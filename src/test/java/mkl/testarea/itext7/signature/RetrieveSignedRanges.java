package mkl.testarea.itext7.signature;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.io.source.RASInputStream;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.PdfSignature;
import com.itextpdf.signatures.SignatureUtil;

/**
 * @author mkl
 */
public class RetrieveSignedRanges {
    final static File RESULT_FOLDER = new File("target/test-outputs", "signature");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/47092557/how-can-i-get-byterange-with-itext7">
     * How can I get ByteRange with iText7?
     * </a>
     * <p>
     * This test shows how to extract the signed byte ranges of signatures in a PDF.
     * The code essentially is taken from the iText {@link SignatureUtil} class.
     * </p>
     */
    @Test
    public void testExtractSignedBytes() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("BLANK-signed.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfDocument pdfDocument = new PdfDocument(pdfReader);) {
            SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);
            for (String name : signatureUtil.getSignatureNames())
            {
                PdfSignature signature = signatureUtil.getSignature(name);
                PdfArray b = signature.getByteRange();
                RandomAccessFileOrArray rf = pdfReader.getSafeFile();
                try (   InputStream rg = new RASInputStream(new RandomAccessSourceFactory().createRanged(rf.createSourceView(), SignatureUtil.asLongArray(b)));
                        OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "BLANK-signed-ranges-" + name + ".bin"));) {
                    byte[] buf = new byte[8192];
                    int rd;
                    while ((rd = rg.read(buf, 0, buf.length)) > 0) {
                        result.write(buf, 0, rd);
                    }
                }
            }
        }
    }
}
