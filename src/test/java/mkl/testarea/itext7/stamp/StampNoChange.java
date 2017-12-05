package mkl.testarea.itext7.stamp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.signatures.IExternalSignatureContainer;
import com.itextpdf.signatures.PdfSigner;

/**
 * @author mkl
 */
public class StampNoChange
{
    final static File RESULT_FOLDER = new File("target/test-outputs", "stamp");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="http://stackoverflow.com/questions/39937615/itext-7-pdfdictionary-released-prematurely">
     * iText 7: PdfDictionary released prematurely
     * </a>
     * <br/>
     * <a href="https://mega.nz/#!bh90VI4S!bbjRXOm3EAc2BA-OQPsJKZ5Sei9Xfu9JQl72M0YnYg0">
     * prematureReleaseExample.pdf
     * </a>
     * <p>
     * Indeed, this simple no-change stamping fails with a {@link NullPointerException}
     * due to a structure tree node already been flushed. The cause is that a few nodes
     * appear multiple times in the structure tree of the document at hand.
     * </p>
     * <p>
     * iText can be hardened against this problem by adding an `isFlushed` test to
     * {@link PdfStructTreeRoot#flushAllKids(IPdfStructElem)}:
     * </p>
     * <pre>
     * private void flushAllKids(IPdfStructElem elem) {
     *     for (IPdfStructElem kid : elem.getKids()) {
     *         if (kid instanceof PdfStructElem) {
     *             if (!((PdfStructElem) kid).isFlushed())
     *             {
     *                 flushAllKids(kid);
     *                 ((PdfStructElem) kid).flush();
     *             }
     *         }
     *     }
     * }
     * </pre>
     */
    @Test
    public void testPrematureReleaseExample() throws IOException
    {
        File target = new File(RESULT_FOLDER, "prematureReleaseExample-asis.pdf");
        try (   InputStream resource = getClass().getResourceAsStream("prematureReleaseExample.pdf");
                OutputStream dest = new FileOutputStream(target))
        {
            PdfReader reader = new PdfReader(resource);
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument doc = new PdfDocument(reader, writer);
            doc.close();
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/47281240/append-mode-requires-a-document-without-errors-even-if-recovery-is-possible">
     * Append mode requires a document without errors, even if recovery is possible
     * </a>
     * <br/>
     * <a href="https://www.dropbox.com/s/i7eeamw9xouf76l/word.pdf?dl=0">
     * word.pdf
     * </a>
     * <p>
     * This test reproduces the issue observed by the OP: A first stamping
     * seems to work but the second one throws an error. The cause is that
     * the first stamping already created a broken result which makes the
     * second one stumble.
     * </p>
     * @see #testAppendWordSigning()
     * @see #testNoAppendWord()
     */
    @Test
    public void testAppendWord() throws IOException
    {
        File target = new File(RESULT_FOLDER, "word-append.pdf");
        try (   InputStream resource = getClass().getResourceAsStream("word.pdf");
                OutputStream dest = new FileOutputStream(target))
        {
            PdfReader reader = new PdfReader(resource);
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument doc = new PdfDocument(reader, writer, new StampingProperties().useAppendMode());
            doc.close();
        }
        File secondTarget = new File(RESULT_FOLDER, "word-append-again.pdf");
        try (   OutputStream dest = new FileOutputStream(secondTarget))
        {
            PdfReader reader = new PdfReader(target);
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument doc = new PdfDocument(reader, writer, new StampingProperties().useAppendMode());
            doc.close();
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/47281240/append-mode-requires-a-document-without-errors-even-if-recovery-is-possible">
     * Append mode requires a document without errors, even if recovery is possible
     * </a>
     * <br/>
     * <a href="https://www.dropbox.com/s/i7eeamw9xouf76l/word.pdf?dl=0">
     * word.pdf
     * </a>
     * <p>
     * This test shows that the issue is not bound to the signing use case
     * during which the OP observed it: Signing simply produces the same
     * fault in the result document as arbitrary stamping does in the test
     * {@link #testAppendWord()}.
     * </p>
     */
    @Test
    public void testAppendWordSigning() throws IOException, GeneralSecurityException
    {
        File target = new File(RESULT_FOLDER, "word-signed.pdf");
        try (   InputStream resource = getClass().getResourceAsStream("word.pdf");
                OutputStream dest = new FileOutputStream(target))
        {
            PdfReader reader = new PdfReader(resource);
            PdfSigner signer = new PdfSigner(reader, dest, true);
            signer.signExternalContainer(new IExternalSignatureContainer() {
                @Override
                public byte[] sign(InputStream data) throws GeneralSecurityException {
                    return "dummy signature".getBytes();
                }
                
                @Override
                public void modifySigningDictionary(PdfDictionary signDic) {
                }
            }, 4096);
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/47281240/append-mode-requires-a-document-without-errors-even-if-recovery-is-possible">
     * Append mode requires a document without errors, even if recovery is possible
     * </a>
     * <br/>
     * <a href="https://www.dropbox.com/s/i7eeamw9xouf76l/word.pdf?dl=0">
     * word.pdf
     * </a>
     * <p>
     * This test shows that the issue is related to the append
     * mode: Without appending everything works just fine.
     * </p>
     * @see #testAppendWord()
     */
    @Test
    public void testNoAppendWord() throws IOException
    {
        File target = new File(RESULT_FOLDER, "word-stamp.pdf");
        try (   InputStream resource = getClass().getResourceAsStream("word.pdf");
                OutputStream dest = new FileOutputStream(target))
        {
            PdfReader reader = new PdfReader(resource);
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument doc = new PdfDocument(reader, writer);
            doc.close();
        }
        File secondTarget = new File(RESULT_FOLDER, "word-stamp-again.pdf");
        try (   OutputStream dest = new FileOutputStream(secondTarget))
        {
            PdfReader reader = new PdfReader(target);
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument doc = new PdfDocument(reader, writer);
            doc.close();
        }
    }
}
