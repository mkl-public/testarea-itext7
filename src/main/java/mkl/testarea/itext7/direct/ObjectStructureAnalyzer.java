package mkl.testarea.itext7.direct;

import java.io.IOException;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.itextpdf.io.source.ByteBuffer;
import com.itextpdf.io.source.IRandomAccessSource;
import com.itextpdf.io.source.PdfTokenizer;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.ReaderProperties;

/**
 * <p>
 * This class tries to find all indirect objects in a PDF, also those in
 * older revisions of a document replaced in the current revision, and
 * even those referenced in no cross references. 
 * </p>
 * <p>
 * It is in particular designed for detecting PDFs that might be used in
 * hide-and-replace Shadow Attacks as proposed by the Ruhr Uni Bochum, see
 * https://www.pdf-insecurity.org/signature-shadow/shadow-attacks.html
 * </p>
 * 
 * @see ExtPdfIndirectReference
 * @author mkl
 */
public class ObjectStructureAnalyzer extends PdfReader {
    public ObjectStructureAnalyzer(IRandomAccessSource byteSource, ReaderProperties properties) throws IOException {
        super(byteSource, properties);
        setUnethicalReading(true);
    }

    /**
     * <p>
     * This method returns a multi map from integer object numbers to
     * {@link ExtPdfIndirectReference} instances representing indirect
     * objects with that object number found in the PDF.
     * </p>
     * <p>
     * Some of these objects may not be referenced from any cross references,
     * e.g. they might be first versions of some object already written to
     * file when a change to them became necessary, triggering them to be
     * written a second time. They also might be inserted by design to execute
     * a hide-and-replace Shadow Attack. In either case they can probably be
     * abused for such an attack.
     * </p>
     */
    public Multimap<Integer, ExtPdfIndirectReference> findIndirectObjects() throws IOException {
        Multimap<Integer, ExtPdfIndirectReference> xref = MultimapBuilder.treeKeys().arrayListValues().build();
        tokens.seek(0);
        ByteBuffer buffer = new ByteBuffer(24);
        PdfTokenizer lineTokeniser = new PdfTokenizer(new RandomAccessFileOrArray(new ReusableRandomAccessSource(buffer)));
        int rev = 0;
        for (; ; ) {
            long pos = tokens.getPosition();
            buffer.reset();

            // added boolean because of mailing list issue (17 Feb. 2014)
            if (!tokens.readLineSegment(buffer, true))
                break;
            if (buffer.get(0) == 's') {
                byte[] startxref = "startxref".getBytes();
                boolean isStartxref = false;
                if (startxref.length <= buffer.size()) {
                    isStartxref = true;
                    for (int i = 0; i < startxref.length; i++) {
                        if (startxref[i] != buffer.get(i))
                            isStartxref = false;
                    }
                }

                if (isStartxref)
                    rev++;
            } else if (buffer.get(0) >= '0' && buffer.get(0) <= '9') {
                int[] obj = PdfTokenizer.checkObjectStart(lineTokeniser);
                if (obj == null)
                    continue;
                int num = obj[0];
                int gen = obj[1];
                ExtPdfIndirectReference ref = new ExtPdfIndirectReference(pdfDocument, num, gen, rev, pos); 

                long posAfter = tokens.getPosition();
                try {
                    PdfObject object = readObject(ref);
                    if (object instanceof PdfStream) {
                        findObjectsInObjectStream((PdfStream) object, xref, rev);
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                } finally {
                    tokens.seek(posAfter);
                }
                xref.put(num, ref);
            }
        }
        return xref;
    }

    void findObjectsInObjectStream(PdfStream objStream, Multimap<Integer, ExtPdfIndirectReference> xref, int rev) throws IOException {
        int first = objStream.getAsNumber(PdfName.First).intValue();
        int n = objStream.getAsNumber(PdfName.N).intValue();
        byte[] bytes = readStreamBytes(objStream, true);
        PdfTokenizer saveTokens = tokens;
        try {
            tokens = new PdfTokenizer(new RandomAccessFileOrArray(new RandomAccessSourceFactory().createSource(bytes)));
            boolean ok = true;
            for (int k = 0; k < n; ++k) {
                ok = tokens.nextToken();
                if (!ok)
                    break;
                if (tokens.getTokenType() != PdfTokenizer.TokenType.Number) {
                    ok = false;
                    break;
                }
                int objNumber = tokens.getIntValue();
                ok = tokens.nextToken();
                if (!ok)
                    break;
                if (tokens.getTokenType() != PdfTokenizer.TokenType.Number) {
                    ok = false;
                    break;
                }
                int address = tokens.getIntValue() + first;

                ExtPdfIndirectReference ref = new ExtPdfIndirectReference(pdfDocument, objNumber, 0, rev, k);
                ref.setObjStreamNumber(objStream.getIndirectReference().getObjNumber());
                xref.put(objNumber, ref);
            }

        } finally {
            tokens = saveTokens;
        }

    }
}
