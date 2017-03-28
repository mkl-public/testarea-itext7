package mkl.testarea.itext7.annotate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.ByteStreams;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfStampAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

/**
 * @author mkl
 */
public class AddStampAnnotation
{
    final static File RESULT_FOLDER = new File("target/test-outputs", "annotate");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="http://stackoverflow.com/questions/43052760/itext-7-generated-stamp-breaks-upon-rotation">
     * iText 7: Generated Stamp Breaks Upon Rotation
     * </a>
     * <p>
     * Trial and error with Adobe Reader DC appears to suggest that this
     * PDF viewer wants to recreate the appearance when rotating a stamp
     * annotation, assuming that it knows how to do that from its own
     * resources, unless the name starts with a '#' character.
     * </p>
     */
    @Test
    public void testAddCustomStampAnnotation() throws IOException
    {
        try (   InputStream resourceStream = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf");
                OutputStream outputStream = new FileOutputStream(new File(RESULT_FOLDER, "test-with-stamp.pdf"));
                PdfReader pdfReader = new PdfReader(resourceStream);
                PdfWriter pdfWriter = new PdfWriter(outputStream);
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter);
                InputStream imageStream = getClass().getResourceAsStream("Willi-1.jpg"))
        {
            ImageData imageData = ImageDataFactory.create(ByteStreams.toByteArray(imageStream));
            float iWidth = imageData.getWidth();
            float iHeight = imageData.getHeight();

            Rectangle crop = pdfDocument.getFirstPage().getCropBox();
            Rectangle location = new Rectangle(crop.getLeft(), crop.getBottom(), iWidth/4, iHeight/4);

            //PdfStampAnnotation stamp = new PdfStampAnnotation(location).setStampName(new PdfName("#zKzrX95V9NYDDQGyrLjmOA"));
            PdfStampAnnotation stamp = new PdfStampAnnotation(location).setStampName(new PdfName("#Logo"));

            PdfFormXObject xObj = new PdfFormXObject(new Rectangle(iWidth, iHeight));
            PdfCanvas canvas = new PdfCanvas(xObj, pdfDocument);
            canvas.addImage(imageData, 0, 0, iWidth, false);
            stamp.setNormalAppearance(xObj.getPdfObject());

            stamp.put(PdfName.Type, PdfName.Annot);
            stamp.setFlags(PdfAnnotation.PRINT);

            pdfDocument.getFirstPage().addAnnotation(stamp);
        }
    }

}
