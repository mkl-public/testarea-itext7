package mkl.testarea.itext7.meta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;

/**
 * This is a simple tool to compare structurally similar PDFs, e.g. an
 * original PDF and one derived from it with (small) changes.
 * 
 * @author mkl
 */
public class PdfCompare {
    public static void main(String[] args) throws IOException {
        System.out.printf("Comparing:\n* %s\n* %s\n", args[0], args[1]);
        try (   PdfDocument pdfDocument1 = new PdfDocument(new PdfReader(args[0]));
                PdfDocument pdfDocument2 = new PdfDocument(new PdfReader(args[1]))  ) {
            PdfCompare pdfCompare = new PdfCompare(pdfDocument1, pdfDocument2);
            pdfCompare.compare();

            List<Difference> differences = pdfCompare.getDifferences();
            if (differences == null || differences.isEmpty()) {
                System.out.println("No differencces found.");
            } else {
                System.out.printf("%d differencces found:\n", differences.size());
                for (Difference difference : pdfCompare.getDifferences()) {
                    for (String element : difference.getPath()) {
                        System.out.print(element);
                    }
                    System.out.printf(" - %s\n", difference.getDescription());
                }
            }
        }
    }

    public interface Difference {
        List<String> getPath();
        String getDescription();
    }

    public PdfCompare(PdfDocument pdfDocument1, PdfDocument pdfDocument2) {
        trailer1 = pdfDocument1.getTrailer();
        trailer2 = pdfDocument2.getTrailer();
    }

    public void compare() {
        LOGGER.info("Starting comparison");
        try {
            compared.clear();
            differences.clear();
            compare(trailer1, trailer2, Collections.singletonList("trailer"));
        } finally {
            LOGGER.info("Finished comparison");
        }
    }

    public List<Difference> getDifferences() {
        return differences;
    }

    class DifferenceImplSimple implements Difference {
        DifferenceImplSimple(List<String> path, String description) {
            this.path = path;
            this.description = description;
        }

        @Override public List<String> getPath()     { return path;          }
        @Override public String getDescription()    { return description;   }

        final List<String> path;
        final String description;
    }

    void compare(PdfObject object1, PdfObject object2, List<String> path) {
        LOGGER.debug("Comparing objects at {}.", path);
        if (object1 == null && object2 == null)
        {
            LOGGER.debug("Both objects are null at {}.", path);
            return;
        }
        if (object1 == null) {
            differences.add(new DifferenceImplSimple(path, "Missing in document 1"));
            LOGGER.info("Object in document 1 is missing at {}.", path);
            return;
        }
        if (object2 == null) {
            differences.add(new DifferenceImplSimple(path, "Missing in document 2"));
            LOGGER.info("Object in document 2 is missing at {}.", path);
            return;
        }

        if (object1.getType() != object2.getType()) {
            differences.add(new DifferenceImplSimple(path,
                    String.format("Type difference, %s in document 1 and %s in document 2",
                            getTypeName(object1.getType()), getTypeName(object2.getType()))));
            LOGGER.info("Objects have different types at {}, {} and {}.", path, getTypeName(object1.getType()), getTypeName(object2.getType()));
            return;
        }

        switch (object1.getType()) {
        case PdfObject.ARRAY:
            compareContents((PdfArray) object1, (PdfArray) object2, path);
            break;
        case PdfObject.DICTIONARY:
            compareContents((PdfDictionary) object1, (PdfDictionary) object2, path);
            break;
        case PdfObject.STREAM:
            compareContents((PdfStream)object1, (PdfStream)object2, path);
            break;
        case PdfObject.BOOLEAN:
        case PdfObject.INDIRECT_REFERENCE:
        case PdfObject.LITERAL:
        case PdfObject.NAME:
        case PdfObject.NULL:
        case PdfObject.NUMBER:
        case PdfObject.STRING:
            compareContentsSimple(object1, object2, path);
            break;
        default:
            differences.add(new DifferenceImplSimple(path, "Unknown object type " + object1.getType() + "; cannot compare"));
            LOGGER.warn("Unknown object type at {}, {}.", path, object1.getType());
            break;
        }
    }

    void compareContents(PdfArray array1, PdfArray array2, List<String> path) {
        int count1 = array1.size();
        int count2 = array2.size();
        if (count1 < count2) {
            differences.add(new DifferenceImplSimple(path, "Document 1 misses " + (count2-count1) + " array entries"));
            LOGGER.info("Array in document 1 is missing {} entries at {} for {}.", (count2-count1), path);
        }
        if (count1 > count2) {
            differences.add(new DifferenceImplSimple(path, "Document 2 misses " + (count1-count2) + " array entries"));
            LOGGER.info("Array in document 2 is missing {} entries at {} for {}.", (count1-count2), path);
        }

        Pair<PdfObject, PdfObject> pair = Pair.of(array1, array2);
        if (compared.containsKey(pair)) {
            LOGGER.debug("Dictionaries already compared at {}, originally at {}.", path, compared.get(pair));
            return;
        }
        compared.put(pair, path);

        int count = Math.min(count1, count2);
        for (int i = 0; i < count; i++) {
            compare(array1.get(i), array2.get(i), join(path, String.format("[%d]", i)));
        }
    }

    void compareContents(PdfDictionary dictionary1, PdfDictionary dictionary2, List<String> path) {
        List<PdfName> missing1 = new ArrayList<PdfName>(dictionary2.keySet());
        missing1.removeAll(dictionary1.keySet());
        if (!missing1.isEmpty()) {
            differences.add(new DifferenceImplSimple(path, "Document 1 misses dictionary entries for " + missing1));
            LOGGER.info("Dictionary in document 1 is missing entries at {} for {}.", path, missing1);
        }

        List<PdfName> missing2 = new ArrayList<PdfName>(dictionary1.keySet());
        missing2.removeAll(dictionary2.keySet());
        if (!missing2.isEmpty()) {
            differences.add(new DifferenceImplSimple(path, "Document 2 misses dictionary entries for " + missing2));
            LOGGER.info("Dictionary in document 2 is missing entries at {} for {}.", path, missing2);
        }

        Pair<PdfObject, PdfObject> pair = Pair.of(dictionary1, dictionary2);
        if (compared.containsKey(pair)) {
            LOGGER.debug("Dictionaries already compared at {}, originally at {}.", path, compared.get(pair));
            return;
        }
        compared.put(pair, path);

        List<PdfName> common = new ArrayList<PdfName>(dictionary1.keySet());
        common.retainAll(dictionary2.keySet());
        for (PdfName name : common) {
            compare(dictionary1.get(name), dictionary2.get(name), join(path, name.toString()));
        }
    }

    void compareContents(PdfStream stream1, PdfStream stream2, List<String> path) {
        compareContents((PdfDictionary)stream1, (PdfDictionary)stream2, path);

        byte[] bytes1 = stream1.getBytes();
        byte[] bytes2 = stream2.getBytes();
        if (!Arrays.equals(bytes1, bytes2)) {
            differences.add(new DifferenceImplSimple(path, "Stream contents differ"));
            LOGGER.info("Stream contents differ at {}.", path);
        }
    }

    void compareContentsSimple(PdfObject object1, PdfObject object2, List<String> path) {
        if (!object1.equals(object2)) {
            differences.add(new DifferenceImplSimple(path, String.format("Object values differ, '%s' and '%s'", object1, object2)));
            LOGGER.info("Object values differ at {}, '{}' and '{}'.", path, object1, object2);
        }
    }

    String getTypeName(byte type) {
        switch (type) {
        case PdfObject.ARRAY:               return "ARRAY";
        case PdfObject.BOOLEAN:             return "BOOLEAN";
        case PdfObject.DICTIONARY:          return "DICTIONARY";
        case PdfObject.LITERAL:             return "LITERAL";
        case PdfObject.INDIRECT_REFERENCE:  return "REFERENCE";
        case PdfObject.NAME:                return "NAME";
        case PdfObject.NULL:                return "NULL";
        case PdfObject.NUMBER:              return "NUMBER";
        case PdfObject.STREAM:              return "STREAM";
        case PdfObject.STRING:              return "STRING";
        default:
            return "UNKNOWN";
        }
    }

    List<String> join(List<String> path, String element) {
        String[] array = path.toArray(new String[path.size() + 1]);
        array[array.length-1] = element;
        return Arrays.asList(array);
    }

    final static Logger LOGGER = LoggerFactory.getLogger(PdfCompare.class);
    final PdfDictionary trailer1;
    final PdfDictionary trailer2;
    final Map<Pair<PdfObject, PdfObject>, List<String>> compared = new HashMap<>();
    final List<Difference> differences = new ArrayList<>();
}
