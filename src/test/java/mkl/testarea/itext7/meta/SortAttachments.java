package mkl.testarea.itext7.meta;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.collection.PdfCollection;
import com.itextpdf.kernel.pdf.collection.PdfCollectionField;
import com.itextpdf.kernel.pdf.collection.PdfCollectionSchema;
import com.itextpdf.kernel.pdf.collection.PdfCollectionSort;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;

/**
 * @author mkl
 */
public class SortAttachments {
    final static File RESULT_FOLDER = new File("target/test-outputs", "meta");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/50323921/in-itext7-how-to-change-attach-files-display-order-by-added-time">
     * In itext7,how to change attach files display order by added time
     * </a>
     * <p>
     * This test only uses the method {@link #attachFile(String, String, List)}
     * which is essentially the OP's original code. A sort order cannot be set.
     * </p>
     * @see #testAttachLikeGeologistedWithCollection()
     */
    @Test
    public void testAttachLikeGeologisted() throws IOException {
        File fileWithoutAttachments = new File(RESULT_FOLDER, "attachments-0-none.pdf");
        File fileWithAttachments = new File(RESULT_FOLDER, "attachments-1-some.pdf");

        try (   InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf")) {
            Files.copy(resource, fileWithoutAttachments.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        attachFile(fileWithoutAttachments.getPath(), fileWithAttachments.getPath(), Arrays.asList(
                new File("src\\test\\resources\\mkl\\testarea\\itext7\\meta\\REJECT_ContainsJavaScript.pdf"),
                new File("src\\test\\resources\\mkl\\testarea\\itext7\\meta\\Lato-Regular.ttf")));
    }

    /**
     * <a href="https://stackoverflow.com/questions/50323921/in-itext7-how-to-change-attach-files-display-order-by-added-time">
     * In itext7,how to change attach files display order by added time
     * </a>
     * <p>
     * In addition to the method {@link #attachFile(String, String, List)}
     * which is essentially the OP's original code, this test makes the PDF
     * a portable collection. A sort order now can be set.
     * </p>
     * @see #testAttachLikeGeologisted()
     */
    @Test
    public void testAttachLikeGeologistedWithCollection() throws IOException, InterruptedException {
        File fileWithoutAttachments = new File(RESULT_FOLDER, "collection-0-none.pdf");
        File fileWithAttachments = new File(RESULT_FOLDER, "collection-1-some.pdf");
        File fileWithMoreAttachments = new File(RESULT_FOLDER, "collection-2-more.pdf");

        try (   InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf");
                PdfReader reader = new PdfReader(resource);
                PdfWriter writer = new PdfWriter(fileWithoutAttachments);
                PdfDocument document = new PdfDocument(reader, writer)) {
            PdfCollection collection = new PdfCollection();
            document.getCatalog().setCollection(collection);
            PdfCollectionSchema schema = new PdfCollectionSchema();
            PdfCollectionField field = new PdfCollectionField("File Name", PdfCollectionField.FILENAME);
            field.setOrder(0);
            schema.addField("Name", field);
            field = new PdfCollectionField("Modification Date", PdfCollectionField.MODDATE);
            field.setOrder(1);
            schema.addField("Modified", field);
            collection.setSchema(schema);
            PdfCollectionSort sort = new PdfCollectionSort("Modified");
            collection.setSort(sort);
        }

        attachFile(fileWithoutAttachments.getPath(), fileWithAttachments.getPath(), Arrays.asList(
                new File("src\\test\\resources\\mkl\\testarea\\itext7\\meta\\REJECT_ContainsJavaScript.pdf"),
                new File("src\\test\\resources\\mkl\\testarea\\itext7\\meta\\Lato-Regular.ttf")));

        Thread.sleep(2000);

        attachFile(fileWithAttachments.getPath(), fileWithMoreAttachments.getPath(), Arrays.asList(
                new File("src\\test\\resources\\mkl\\testarea\\itext7\\content\\document.pdf"),
                new File("src\\test\\resources\\mkl\\testarea\\itext7\\content\\test.pdf")));

    }

    /**
     * <a href="https://stackoverflow.com/questions/50323921/in-itext7-how-to-change-attach-files-display-order-by-added-time">
     * In itext7,how to change attach files display order by added time
     * </a>
     * <p>
     * This is essentially the OP's original code, merely the
     * <code>SysItemfile</code> was replaced by <code>File</code>
     * and exception are not caught anymore.
     * </p>
     * @see #testAttachLikeGeologisted()
     * @see #testAttachLikeGeologistedWithCollection()
     */
    public void attachFile(String src, String dest, List<File> attachmentpaths) throws IOException {
        PdfName name = new PdfName(src);
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));
        List<String> descs = new ArrayList<String>();
        int i = 0;
        int j = 1;
        for (File attachmentpath : attachmentpaths) {
            String filename = attachmentpath.getName();
            //test for the file name
            System.out.println("filename:"+filename);

            if (descs.contains(attachmentpath.getName())) {
                //get the file suffix 
                String suffix = filename.substring(filename.lastIndexOf(".") + 1);
                String realname = filename.substring(0,filename.lastIndexOf("."));
                filename = realname+i+"."+suffix;
                i++;
            } else {
                descs.add(attachmentpath.getName());
            }
            PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(pdfDoc, attachmentpath.getPath(),
                    filename, filename, name, name);
            // the first parameter is discription
            pdfDoc.addFileAttachment(filename, spec);
        }
        pdfDoc.close();
    }
}
