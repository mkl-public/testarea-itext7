package mkl.testarea.itext7.merge;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.property.AreaBreakType;

/**
 * @author mkl
 */
public class CopyPdfsAndImages {
    final static File RESULT_FOLDER = new File("target/test-outputs", "merge");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/68377306/itext-7-java-add-image-on-a-new-page-to-the-end-of-an-existing-pdf-document">
     * itext 7 (java) add image on a new page to the end of an existing pdf document
     * </a>
     * <p>
     * This is a small condensed example of what can happen in the OP's
     * original code. Indeed, images are added on pages of the imported
     * PDFs. But see {@link #testMergeLikeAndreasHuberImproved()}.
     * </p>
     */
    @Test
    public void testMergeLikeAndreasHuber() throws IOException {
        try (   InputStream resourcePdf1 = getClass().getResourceAsStream("/mkl/testarea/itext7/content/_FDA_Requires_Use_of_eCTD_Format_and_Standardized_Study_Data_in_Future_Regulatory_Submissions__Sept.pdf");
                InputStream resourcePdf2 = getClass().getResourceAsStream("/mkl/testarea/itext7/content/Control_of_nitrosamine_impurities_in_sartans__rev.pdf");
                InputStream resourcePdf3 = getClass().getResourceAsStream("/mkl/testarea/itext7/content/EDQM_reports_issues_of_non-compliance_with_tooth__Mac.pdf");
                InputStream resourceImg1 = getClass().getResourceAsStream("/mkl/testarea/itext7/content/Graph.png");
                InputStream resourceImg2 = getClass().getResourceAsStream("/mkl/testarea/itext7/content/Oskar.jpg");
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "PdfsAndImagesLikeAndreasHuber.pdf"))   )
        {
            PdfReader readerPdf1 = new PdfReader(resourcePdf1);
            PdfReader readerPdf2 = new PdfReader(resourcePdf2);
            PdfReader readerPdf3 = new PdfReader(resourcePdf3);

            Image img1 = new Image(ImageDataFactory.create(StreamUtil.inputStreamToArray(resourceImg1)));
            Image img2 = new Image(ImageDataFactory.create(StreamUtil.inputStreamToArray(resourceImg2)));

            PdfWriter writer = new PdfWriter(result);

            try (   PdfDocument pdf1 = new PdfDocument(readerPdf1);
                    PdfDocument pdf2 = new PdfDocument(readerPdf2);
                    PdfDocument pdf3 = new PdfDocument(readerPdf3);
                    PdfDocument target = new PdfDocument(writer);
                    Document document = new Document(target)    )
            {
                pdf1.copyPagesTo(1, pdf1.getNumberOfPages(), target);
                document.add(img1);
                document.add(img2);
                pdf2.copyPagesTo(1, pdf2.getNumberOfPages(), target);
                pdf3.copyPagesTo(1, pdf3.getNumberOfPages(), target);
            }
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/68377306/itext-7-java-add-image-on-a-new-page-to-the-end-of-an-existing-pdf-document">
     * itext 7 (java) add image on a new page to the end of an existing pdf document
     * </a>
     * <p>
     * With the original code, images are added on pages of the imported
     * PDFs, see {@link #testMergeLikeAndreasHuber()}. This code now makes
     * sure that images are drawn on a new page after the currently last one.
     * </p>
     */
    @Test
    public void testMergeLikeAndreasHuberImproved() throws IOException {
        try (   InputStream resourcePdf1 = getClass().getResourceAsStream("/mkl/testarea/itext7/content/_FDA_Requires_Use_of_eCTD_Format_and_Standardized_Study_Data_in_Future_Regulatory_Submissions__Sept.pdf");
                InputStream resourcePdf2 = getClass().getResourceAsStream("/mkl/testarea/itext7/content/Control_of_nitrosamine_impurities_in_sartans__rev.pdf");
                InputStream resourcePdf3 = getClass().getResourceAsStream("/mkl/testarea/itext7/content/EDQM_reports_issues_of_non-compliance_with_tooth__Mac.pdf");
                InputStream resourceImg1 = getClass().getResourceAsStream("/mkl/testarea/itext7/content/Graph.png");
                InputStream resourceImg2 = getClass().getResourceAsStream("/mkl/testarea/itext7/content/Oskar.jpg");
                OutputStream result = new FileOutputStream(new File(RESULT_FOLDER, "PdfsAndImagesLikeAndreasHuberImproved.pdf"))   )
        {
            PdfReader readerPdf1 = new PdfReader(resourcePdf1);
            PdfReader readerPdf2 = new PdfReader(resourcePdf2);
            PdfReader readerPdf3 = new PdfReader(resourcePdf3);

            Image img1 = new Image(ImageDataFactory.create(StreamUtil.inputStreamToArray(resourceImg1)));
            Image img2 = new Image(ImageDataFactory.create(StreamUtil.inputStreamToArray(resourceImg2)));

            PdfWriter writer = new PdfWriter(result);

            try (   PdfDocument pdf1 = new PdfDocument(readerPdf1);
                    PdfDocument pdf2 = new PdfDocument(readerPdf2);
                    PdfDocument pdf3 = new PdfDocument(readerPdf3);
                    PdfDocument target = new PdfDocument(writer);
                    Document document = new Document(target)    )
            {
                pdf1.copyPagesTo(1, pdf1.getNumberOfPages(), target);
                document.add(new AreaBreak(AreaBreakType.LAST_PAGE));
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                document.add(img1);
                document.add(new AreaBreak(AreaBreakType.LAST_PAGE));
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                document.add(img2);
                pdf2.copyPagesTo(1, pdf2.getNumberOfPages(), target);
                pdf3.copyPagesTo(1, pdf3.getNumberOfPages(), target);
            }
        }
    }
}
