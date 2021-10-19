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
import com.itextpdf.kernel.pdf.PdfArray;
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
public class AddRotatedAnnotation
{
    final static File RESULT_FOLDER = new File("target/test-outputs", "annotate");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="http://stackoverflow.com/questions/43374635/itextsharp-7-rotating-a-stamp-or-annotation">
     * iTextsharp 7: Rotating a Stamp or Annotation
     * </a>
     * <p>
     * There basically are two ways to add a rotation with an image which is to appear rotated,
     * either rotate the image or rotate the appearance. This test does the former.
     * </p>
     * 
     * @see #testRotateMatrix()
     */
    @Test
    public void testRotateImage() throws IOException
    {
        try (   InputStream resourceStream = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf");
                OutputStream outputStream = new FileOutputStream(new File(RESULT_FOLDER, "test-with-rotatedImage.pdf"));
                PdfReader pdfReader = new PdfReader(resourceStream);
                PdfWriter pdfWriter = new PdfWriter(outputStream);
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter);
                InputStream imageStream = getClass().getResourceAsStream("Willi-1.jpg"))
        {
            ImageData imageData = ImageDataFactory.create(ByteStreams.toByteArray(imageStream));
            float iWidth = imageData.getWidth();
            float iHeight = imageData.getHeight();

            Rectangle crop = pdfDocument.getFirstPage().getCropBox();
            // The content image of the annotation shall be rotated, so switch width and height
            Rectangle location = new Rectangle(crop.getLeft(), crop.getBottom(), iHeight/4, iWidth/4);

            PdfStampAnnotation stamp = new PdfStampAnnotation(location).setStampName(new PdfName("#Logo"));

            // The content image in the appearance shall be rotated, so switch width and height
            PdfFormXObject xObj = new PdfFormXObject(new Rectangle(iHeight, iWidth));
            PdfCanvas canvas = new PdfCanvas(xObj, pdfDocument);
            // Insert image using rotation transformation matrix
            canvas.addImageWithTransformationMatrix(imageData, 0, iWidth, -iHeight, 0, iHeight, 0);
            stamp.setNormalAppearance(xObj.getPdfObject());

            stamp.put(PdfName.Type, PdfName.Annot);
            stamp.setFlags(PdfAnnotation.PRINT);

            pdfDocument.getFirstPage().addAnnotation(stamp);
        }
    }

    /**
     * <a href="http://stackoverflow.com/questions/43374635/itextsharp-7-rotating-a-stamp-or-annotation">
     * iTextsharp 7: Rotating a Stamp or Annotation
     * </a>
     * <p>
     * There basically are two ways to add a rotation with an image which is to appear rotated,
     * either rotate the image or rotate the appearance. This test does the latter.
     * </p>
     * 
     * @see #testRotateImage()
     */
    @Test
    public void testRotateMatrix() throws IOException
    {
        try (   InputStream resourceStream = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf");
                OutputStream outputStream = new FileOutputStream(new File(RESULT_FOLDER, "test-with-rotatedMatrix.pdf"));
                PdfReader pdfReader = new PdfReader(resourceStream);
                PdfWriter pdfWriter = new PdfWriter(outputStream);
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter);
                InputStream imageStream = getClass().getResourceAsStream("Willi-1.jpg"))
        {
            ImageData imageData = ImageDataFactory.create(ByteStreams.toByteArray(imageStream));
            float iWidth = imageData.getWidth();
            float iHeight = imageData.getHeight();

            Rectangle crop = pdfDocument.getFirstPage().getCropBox();
            // The appearance (with the upright image) of the annotation shall be rotated, so switch width and height
            Rectangle location = new Rectangle(crop.getLeft(), crop.getBottom(), iHeight/4, iWidth/4);

            PdfStampAnnotation stamp = new PdfStampAnnotation(location).setStampName(new PdfName("#Logo"));

            // The content image in the appearance shall be upright, so don't switch width and height
            PdfFormXObject xObj = new PdfFormXObject(new Rectangle(iWidth, iHeight));
            // The appearance shall be rotated
            xObj.put(PdfName.Matrix, new PdfArray(new int[]{0, 1, -1, 0, 0, 0}));
            PdfCanvas canvas = new PdfCanvas(xObj, pdfDocument);
            // Insert upright image
            canvas.addImageWithTransformationMatrix(imageData, iWidth, 0, 0, iHeight, 0, 0);
            stamp.setNormalAppearance(xObj.getPdfObject());

            stamp.put(PdfName.Type, PdfName.Annot);
            stamp.setFlags(PdfAnnotation.PRINT);

            pdfDocument.getFirstPage().addAnnotation(stamp);
        }
    }
}
