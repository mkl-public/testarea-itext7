package mkl.testarea.itext7.direct;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.pdf.ReaderProperties;

import mkl.testarea.itext7.extract.FieldValues;
import mkl.testarea.itext7.extract.WidgetAnalyzer;

/**
 * This tool checks PDFs for the presence of multiple indirect objects
 * with the same object number in the same PDF revision. Such constructs
 * are used in the hide-and-replace Shadow Attack to manipulate signed
 * PDFs without PDF viewers/validators like Adobe Acrobat Reader warning
 * about any changes.
 * 
 * @author mkl
 */
public class SuspectPdfFinder {
    public static void main(String[] args) {
        SuspectPdfFinder finder = new SuspectPdfFinder();
        finder.maxRevisions = 3;
        finder.maxObjNumbersPerRevision = 10;
        finder.check(args);
        System.out.println("\ndone!");
    }

    void check(String[] filesOrDirs) {
        for (String file : filesOrDirs) {
            if (new File(file).isDirectory()) {
                String[] files = Arrays.asList(new File(file).list()).stream().map(f -> new File(file, f).toString()).toArray(i -> new String[i]);
                countDirs++; printProgress();
                check(files);
            } else {
                check(file);
            }
        }
    }

    void check(String file) {
        try (   ObjectStructureAnalyzer objectAnalyzer = new ObjectStructureAnalyzer(new RandomAccessSourceFactory().createBestSource(file), new ReaderProperties());
                WidgetAnalyzer widgetAnalyzer = new WidgetAnalyzer(new RandomAccessSourceFactory().createBestSource(file), new ReaderProperties())  ) {
            Multimap<Integer, Integer> suspiciousObjects = MultimapBuilder.treeKeys().treeSetValues().build();
            Multimap<Integer, ExtPdfIndirectReference> references = objectAnalyzer.findIndirectObjects();
            for (Integer num : references.keySet()) {
                Collection<ExtPdfIndirectReference> theseReferences = references.get(num);
                if (theseReferences.size() > 1) {
                    List<Integer> revs = theseReferences.stream().map(ExtPdfIndirectReference::getRevNr).collect(Collectors.toList());
                    revs.sort(null);
                    int prevRevision = -1;
                    for (int rev : revs) {
                        if (prevRevision == rev) {
                            suspiciousObjects.put(rev, num);
                        }
                        prevRevision = rev;
                    }
                }
            }

            Map<String, FieldValues<String>> suspiciousFields = new TreeMap<String, FieldValues<String>>();
            Map<String, FieldValues<String>> fieldValues = widgetAnalyzer.findFieldValues();
            for (Map.Entry<String, FieldValues<String>> entry : fieldValues.entrySet()) {
                FieldValues<String> values = entry.getValue();
                Set<String> widgetValues = values.getWidgetValues();
                if (widgetValues.size() > 1 || (widgetValues.size() == 1 && !values.getActualValue().equals(widgetValues.iterator().next())))
                    suspiciousFields.put(entry.getKey(), values);
            }


            boolean suspicious = !(suspiciousObjects.isEmpty() && suspiciousFields.isEmpty());
            if (suspicious) {
                if (!lastPrintedSuspicion) {
                    System.out.println();
                    lastPrintedSuspicion = true;
                }
                System.out.printf("\n\"%s\"\n", file);

                if (!suspiciousObjects.isEmpty()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(" * has ")
                                 .append(suspiciousObjects.keySet().size())
                                 .append(" revision(s) with multiple objects with the same object number:\n");
                    int revCount = 0;
                    for (int revision : suspiciousObjects.keySet()) {
                        stringBuilder.append("   - ");
                        if (++revCount > maxRevisions) {
                            stringBuilder.append("...\n");
                            break;
                        }
                        stringBuilder.append("Revision ")
                                     .append(revision)
                                     .append(" contains multiple object for the object numbers");
                        int objNumberCount = 0;
                        for (int objNumber : suspiciousObjects.get(revision)) {
                            stringBuilder.append(objNumberCount > 0 ? ", " : " ");
                            if (++objNumberCount > maxObjNumbersPerRevision) {
                                stringBuilder.append("...\n");
                                break;
                            }
                            stringBuilder.append(objNumber);
                        }
                        stringBuilder.append('\n');
                    }

                    System.out.print(stringBuilder.toString());
                }

                if (!suspiciousFields.isEmpty()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(" * has ")
                                 .append(suspiciousFields.keySet().size())
                                 .append(" field(s) with differing appearance texts and values:\n");
                    int fieldCount = 0;
                    for (Map.Entry<String, FieldValues<String>> entry : suspiciousFields.entrySet()) {
                        FieldValues<String> values = entry.getValue();
                        stringBuilder.append("   - ");
                        if (++fieldCount > maxFields) {
                            stringBuilder.append("...\n");
                            break;
                        }
                        stringBuilder.append("Field \"")
                                     .append(escapeValue(entry.getKey()))
                                     .append("\" has value \"")
                                     .append(escapeValue(values.getActualValue()))
                                     .append("\" and appearance texts");
                        int objValueCount = 0;
                        for (String value : values.getWidgetValues()) {
                            stringBuilder.append(objValueCount > 0 ? ", " : " ");
                            if (++objValueCount > maxObjNumbersPerRevision) {
                                stringBuilder.append("...\n");
                                break;
                            }
                            stringBuilder.append('"')
                                         .append(escapeValue(value))
                                         .append('"');
                        }
                        stringBuilder.append('\n');
                    }

                    System.out.print(stringBuilder.toString());
                }

                countSuspiciousPdfs++;
            } else {
                printProgress();
            }
            countPdfs++;
        } catch (Exception e) {
            countOtherFiles++;
            printProgress();
        }
    }

    void printProgress() {
        if (lastPrintedSuspicion) {
            System.out.println();
            lastPrintedSuspicion = false;
        }
        System.out.printf("\rPDFs (suspicious): %d (%d), other files: %d, folders: %d", countPdfs, countSuspiciousPdfs, countOtherFiles, countDirs);
    }

    String escapeValue(String value) {
        if (value != null) {
            value = value.replace("\\", "\\\\").replace("\r", "\\r").replace("\n", "\\n");
        }
        return value;
    }

    int maxRevisions = 3;
    int maxObjNumbersPerRevision = 10;
    int maxFields = 3;
    int maxValues = 10;

    boolean lastPrintedSuspicion = false;
    int countDirs = 0;
    int countOtherFiles = 0;
    int countPdfs = 0;
    int countSuspiciousPdfs = 0;
}
