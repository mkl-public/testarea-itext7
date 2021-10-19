package mkl.testarea.itext7.content;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

/**
 * @author mkl
 */
public class FixedPositionLayouts {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/50440921/what-is-the-best-way-to-form-this-layout-using-itext7">
     * What is the best way to form this layout using iText7?
     * </a>
     * <p>
     * This test shows how to use block element fixed positions and heights
     * to arrange a number of tables for a static layout.
     * </p>
     */
    @Test
    public void testFixedTablePositions() throws IOException {
        try (   PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new File(RESULT_FOLDER, "FixedTablePositions.pdf")));
                Document document = new Document(pdfDocument)) {
            PageSize pageSize = pdfDocument.getDefaultPageSize();
            Table table = new Table(1);
            table.addCell("table 1 - 1");
            table.addCell("table 1 - 2");
            table.setFixedPosition(pageSize.getLeft() + 30, pageSize.getTop() - 75, pageSize.getWidth() - 60);
            table.setHeight(45);
            document.add(table);

            table = new Table(UnitValue.createPercentArray(new float[] {40, 60}));
            table.addCell("table 2 - 1");
            table.addCell("table 2 - 2");
            table.setFixedPosition(pageSize.getLeft() + 30, pageSize.getTop() - 265, (pageSize.getWidth() - 70) / 2);
            table.setHeight(185);
            document.add(table);

            table = new Table(UnitValue.createPercentArray(new float[] {20, 50, 30}));
            table.addCell("table 4 - 1");
            table.addCell("table 4 - 2");
            table.addCell("table 4 - 3");
            table.setFixedPosition(pageSize.getLeft() + 30, pageSize.getTop() - 720, (pageSize.getWidth() - 70) / 2);
            table.setHeight(450);
            document.add(table);

            table = new Table(1);
            table.addCell("table 6");
            table.setFixedPosition(pageSize.getLeft() + 30, pageSize.getTop() - 810, (pageSize.getWidth() - 70) / 2);
            table.setHeight(85);
            document.add(table);

            table = new Table(UnitValue.createPercentArray(new float[] {20, 40, 20, 20}));
            table.addCell("table 3 - 1");
            table.addCell("table 3 - 2");
            table.addCell("table 3 - 3");
            table.addCell("table 3 - 4");
            table.setFixedPosition(pageSize.getRight() - (pageSize.getWidth() - 10) / 2, pageSize.getTop() - 345, (pageSize.getWidth() - 70) / 2);
            table.setHeight(265);
            document.add(table);

            table = new Table(1);
            table.addCell("table 5 - 1");
            table.addCell("table 5 - 2");
            table.setFixedPosition(pageSize.getRight() - (pageSize.getWidth() - 10) / 2, pageSize.getTop() - 640, (pageSize.getWidth() - 70) / 2);
            table.setHeight(290);
            document.add(table);

            table = new Table(UnitValue.createPercentArray(new float[] {20, 50, 30}));
            table.addCell("table 7 - 1");
            table.addCell("table 7 - 2");
            table.addCell("table 7 - 3");
            table.setFixedPosition(pageSize.getRight() - (pageSize.getWidth() - 10) / 2, pageSize.getTop() - 810, (pageSize.getWidth() - 70) / 2);
            table.setHeight(165);
            document.add(table);
        }
    }

}
