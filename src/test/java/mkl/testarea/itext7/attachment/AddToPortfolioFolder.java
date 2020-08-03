package mkl.testarea.itext7.attachment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.collection.PdfCollection;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;

public class AddToPortfolioFolder {
    final static File RESULT_FOLDER = new File("target/test-outputs", "attachment");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <p>
     * This test adds a new attachment to the existing folder <code>/test</code>
     * in the portfolio <code>container.pdf</code>.
     * </p>
     */
    @Test
    public void testAddToTestInContainer() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("container.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfWriter pdfWriter = new PdfWriter(new File(RESULT_FOLDER, "container-AddInTest.pdf"));
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter) ) {
            PdfCollection pdfCollection = pdfDocument.getCatalog().getCollection();
            Map<File, Integer> folders = collectFolders(pdfCollection);

            Integer testId = folders.get(new File("/test"));
            Assert.assertNotNull("Folder '/test' not found in collection", testId);

            String attachmentName = "Example.pdf";
            try (InputStream file = getClass().getResourceAsStream("container.pdf")) {
                byte[] fileBytes = StreamUtil.inputStreamToArray(file);
                PdfFileSpec fileSpec = PdfFileSpec.createEmbeddedFileSpec(pdfDocument, fileBytes, "Description of " + attachmentName,
                        attachmentName + " (displayed)", PdfName.ApplicationPdf, null, new PdfName("MKLx_Example"));
                pdfDocument.addFileAttachment(String.format("<%s>%s", testId, attachmentName), fileSpec);
            }

        }
    }

    /**
     * <p>
     * This test adds a new attachment to the non-existing sub folder
     * <code>test2</code> of the existing folder <code>/test</code>
     * in the portfolio <code>container.pdf</code>.
     * </p>
     */
    @Test
    public void testAddToTestTest2InContainer() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("container.pdf");
                PdfReader pdfReader = new PdfReader(resource);
                PdfWriter pdfWriter = new PdfWriter(new File(RESULT_FOLDER, "container-AddInTestTest2.pdf"));
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter) ) {
            PdfCollection pdfCollection = pdfDocument.getCatalog().getCollection();
            Map<File, Integer> folders = collectFolders(pdfCollection);

            File folder = new File("/test/test2");
            Integer testId = folders.get(folder);
            Assert.assertNull("Folder '/test/test2' unexpectedly found in collection", testId);

            createFolder(pdfDocument, pdfCollection, folders, folder);
            testId = folders.get(folder);
            Assert.assertNotNull("Folder '/test/test2' not found in collection", testId);

            String attachmentName = "Example.pdf";
            try (InputStream file = getClass().getResourceAsStream("container.pdf")) {
                byte[] fileBytes = StreamUtil.inputStreamToArray(file);
                PdfFileSpec fileSpec = PdfFileSpec.createEmbeddedFileSpec(pdfDocument, fileBytes, "Description of " + attachmentName,
                        attachmentName + " (displayed)", PdfName.ApplicationPdf, null, new PdfName("MKLx_Example"));
                pdfDocument.addFileAttachment(String.format("<%s>%s", testId, attachmentName), fileSpec);
            }
        }
    }

    /**
     * Collects a map of folders to their respective IDs.
     */
    Map<File, Integer> collectFolders(PdfCollection pdfCollection) {
        Map<File, Integer> folders = new HashMap<File, Integer>();
        PdfDictionary folderDictionary = pdfCollection.getPdfObject().getAsDictionary(FOLDERS);
        collectFolders(folders, folderDictionary, new File(""));
        return folders;
    }

    void collectFolders(Map<File, Integer> folders, PdfDictionary folder, File baseDir) {
        PdfNumber id = folder.getAsNumber(PdfName.ID);
        PdfString name = folder.getAsString(PdfName.Name);
        File folderDir = new File(baseDir, name.toString());
        folders.put(folderDir, id.intValue());

        PdfDictionary next = folder.getAsDictionary(PdfName.Next);
        if (next != null)
            collectFolders(folders, next, baseDir);
        PdfDictionary child = folder.getAsDictionary(CHILD);
        if (child != null)
            collectFolders(folders, child, folderDir);
    }

    /**
     * Creates a new folder to the collection. Accordingly updates
     * the <code>folders</code> map.
     */
    PdfDictionary createFolder(PdfDocument pdfDocument, PdfCollection pdfCollection, Map<File, Integer> folders, File folder) {
        if (folder == null)
            return null;

        File parentFolder = folder.getParentFile();
        PdfDictionary parent = createFolder(pdfDocument, pdfCollection, folders, parentFolder);

        String folderName = folder.toString();
        if (parentFolder != null)
            folderName = folderName.substring(parentFolder.toString().length());
        if (folderName.startsWith(File.separator))
            folderName = folderName.substring(1);

        if (parent == null) {
            // This is root
            PdfDictionary folderDictionary = pdfCollection.getPdfObject().getAsDictionary(FOLDERS);
            if (folderDictionary != null) {
                PdfString existingRootName = folderDictionary.getAsString(PdfName.Name);
                if (existingRootName == null || !existingRootName.toString().equals(folderName)) {
                    throw new RuntimeException("Invalid root folder for path");
                }
            } else {
                folderDictionary = new PdfDictionary();
                folderDictionary.put(PdfName.ID, new PdfNumber(0));
                folderDictionary.put(PdfName.Name, new PdfString(folderName));
                folderDictionary.put(FREE, new PdfArray(new int[] {1, Integer.MAX_VALUE}));
                pdfCollection.getPdfObject().put(FOLDERS, folderDictionary.makeIndirect(pdfDocument));
                folders.put(folder, 0);
            }
            return folderDictionary;
        } else {
            PdfDictionary child = parent.getAsDictionary(CHILD);
            PdfDictionary lastChild = null;
            while (child != null) {
                lastChild = child;
                PdfString childName = child.getAsString(PdfName.Name);
                if (childName != null && childName.toString().equals(folderName))
                    return child;
                child = child.getAsDictionary(PdfName.Next);
            }

            int nextId = determineNextId(pdfCollection);

            child = new PdfDictionary();
            child.put(PdfName.ID, new PdfNumber(nextId));
            child.put(PdfName.Name, new PdfString(folderName));
            child.makeIndirect(pdfDocument);
            folders.put(folder, nextId);
            if (lastChild == null) {
                parent.put(CHILD, child);
            } else {
                lastChild.put(PdfName.Next, child);
            }
            child.put(PdfName.Parent, parent);
            return child;
        }
    }

    /**
     * Determines the next free ID from the collection and marks it as used.
     */
    int determineNextId(PdfCollection pdfCollection) {
        PdfDictionary folderDictionary = pdfCollection.getPdfObject().getAsDictionary(FOLDERS);
        if (folderDictionary == null)
            return 0;
        PdfArray freeArray = folderDictionary.getAsArray(FREE);
        if (freeArray == null || freeArray.size() < 2)
            throw new RuntimeException("Invalid Free array");
        PdfNumber low = freeArray.getAsNumber(0);
        PdfNumber high = freeArray.getAsNumber(1);
        if (low == null || high == null)
            throw new RuntimeException("Invalid Free array");
        int nextId = low.intValue();
        if (low.intValue() >= high.intValue()) {
            freeArray.remove(0);
            freeArray.remove(0);
        } else {
            freeArray.set(0, new PdfNumber(nextId + 1));
        }
        return nextId;
    }

    final static PdfName FOLDERS = new PdfName("Folders");
    final static PdfName CHILD = new PdfName("Child");
    final static PdfName FREE = new PdfName("Free");
}
