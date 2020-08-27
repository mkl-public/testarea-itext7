package mkl.testarea.itext7.attachment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNameTree;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.utils.PdfMerger;

/**
 * @author mkl
 */
public class FlattenPortfolio {
    final static File RESULT_FOLDER = new File("target/test-outputs", "attachment");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/63578738/how-to-merge-all-pdf-files-from-a-pdf-portfolio-to-a-normal-pdf-file-using-c-sha">
     * How to merge all pdf files from a PDF Portfolio to a normal pdf file using C# iText7?
     * </a>
     * <br/>
     * <a href="http://www.mediafire.com/file/c4tw07wci8swdx9/NPort_5000.pdf/file">
     * NPort_5000.pdf
     * </a>
     * <p>
     * This test shows how to flatten a portfolio, i.e. how to merge all
     * PDF attachments of it into a regular PDF.
     * </p>
     */
    @Test
    public void testFlattenNPort_5000() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("NPort_5000.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfDocument pdfDocument = new PdfDocument(pdfReader)    ) {
            PdfNameTree embeddedFilesTree = pdfDocument.getCatalog().getNameTree(PdfName.EmbeddedFiles);
            Map<String, PdfObject> embeddedFilesMap = embeddedFilesTree.getNames();
            List<PdfStream> embeddedPdfs = new ArrayList<PdfStream>();
            for (Map.Entry<String, PdfObject> entry : embeddedFilesMap.entrySet()) {
                PdfObject pdfObject = entry.getValue();
                if (!(pdfObject instanceof PdfDictionary))
                    continue;
                PdfDictionary filespecDict = (PdfDictionary) pdfObject;
                PdfDictionary embeddedFileDict = filespecDict.getAsDictionary(PdfName.EF);
                if (embeddedFileDict == null)
                    continue;
                PdfStream embeddedFileStream = embeddedFileDict.getAsStream(PdfName.F);
                if (embeddedFileStream == null)
                    continue;
                PdfName subtype = embeddedFileStream.getAsName(PdfName.Subtype);
                if (!PdfName.ApplicationPdf.equals(subtype))
                    continue;
                embeddedPdfs.add(embeddedFileStream);
            }

            Assert.assertFalse("No embedded PDFs found", embeddedPdfs.isEmpty());

            try (   PdfWriter pdfWriter = new PdfWriter(new File(RESULT_FOLDER, "NPort_5000-flat.pdf").getAbsolutePath(), new WriterProperties().setFullCompressionMode(true));
                    PdfDocument flatPdfDocument = new PdfDocument(pdfWriter)    ) {
                PdfMerger pdfMerger = new PdfMerger(flatPdfDocument);
                RandomAccessSourceFactory sourceFactory = new RandomAccessSourceFactory();
                for (PdfStream pdfStream : embeddedPdfs) {
                    try (   PdfReader embeddedReader = new PdfReader(sourceFactory.createSource(pdfStream.getBytes()), new ReaderProperties());
                            PdfDocument embeddedPdfDocument = new PdfDocument(embeddedReader)) {
                        pdfMerger.merge(embeddedPdfDocument, 1, embeddedPdfDocument.getNumberOfPages());
                    }
                }
            }
        }
    }
}
