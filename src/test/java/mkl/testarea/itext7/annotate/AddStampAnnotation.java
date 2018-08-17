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
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfRedactAnnotation;
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

    /**
     * <a href="https://stackoverflow.com/questions/51887277/itext7-stamp-and-redact-annotation-not-working">
     * itext7 stamp and redact annotation not working
     * </a>
     * <br/>
     * <a href="https://www.dropbox.com/s/3jh0e4ttr3q1ne9/calendar_2018-08-04_2018-08-19.pdf?dl=0">
     * calendar_2018-08-04_2018-08-19.pdf
     * </a>
     * <p>
     * Indeed, the redaction and stamp annotations created by the OP are
     * essentially transparent in the viewer and non-existing in a print-out.
     * </p>
     * <p>
     * This can be fixed as shown in the annotations <code>link2x</code> and
     * <code>stamp1x</code> added to the OP's original annotations: By adding
     * a normal appearance and by setting the PRINT flag.
     * </p>
     */
    @Test
    public void addAnnotationsLikeJon() throws IOException {
        try (   InputStream resourceStream = getClass().getResourceAsStream("calendar_2018-08-04_2018-08-19.pdf");
                PdfReader pdfReader = new PdfReader(resourceStream);
                PdfWriter pdfWriter = new PdfWriter(new File(RESULT_FOLDER, "calendar_2018-08-04_2018-08-19.pdf-with-annotations.pdf"));
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter)) {
            PdfAction action = PdfAction.createURI("http://pages.itextpdf.com/ebook-stackoverflow-questions.html");
            Rectangle linkLocation1 = new Rectangle(30, 770, 90, 30);
            PdfAnnotation link1 = new PdfLinkAnnotation(linkLocation1)
                    .setHighlightMode(PdfAnnotation.HIGHLIGHT_INVERT)
                    .setAction(action)
                    .setColor(ColorConstants.RED.getColorValue());
            pdfDocument.getFirstPage().addAnnotation(link1);

            Rectangle linkLocation2 = new Rectangle(30, 670, 30, 90);
            PdfAnnotation link2 = new PdfRedactAnnotation(linkLocation2)
                    .setColor(ColorConstants.BLACK.getColorValue());
            pdfDocument.getFirstPage().addAnnotation(link2);

            Rectangle linkLocation3 = new Rectangle(150, 770, 90, 30);
            PdfAnnotation stamp1 = new PdfStampAnnotation(linkLocation3)
                    .setStampName(new PdfName("Confidential"))
                    .setContents("Landscape").setColor(ColorConstants.BLACK.getColorValue());
            pdfDocument.getFirstPage().addAnnotation(stamp1);

            Rectangle linkLocation4 = new Rectangle(150, 670, 90, 90);
            PdfAnnotation stamp2 = new PdfStampAnnotation(linkLocation4)
                    .setStampName(new PdfName("Confidential"))
                    .setContents("Portrait")
                    .put(PdfName.Rotate, new PdfNumber(90));
            pdfDocument.getFirstPage().addAnnotation(stamp2);

            Rectangle linkLocation5 = new Rectangle(250, 670, 90, 90);
            PdfAnnotation stamp3 = new PdfStampAnnotation(linkLocation5)
                    .setStampName(new PdfName("Confidential"))
                    .setContents("Portrait")
                    .put(PdfName.Rotate, new PdfNumber(45));
            pdfDocument.getFirstPage().addAnnotation(stamp3);

            
            Rectangle linkLocation2x = new Rectangle(150, 470, 30, 90);
            PdfAnnotation link2x = new PdfRedactAnnotation(linkLocation2x)
                    .setColor(ColorConstants.BLACK.getColorValue());
            PdfFormXObject formN = new PdfFormXObject(linkLocation2x);
            PdfCanvas canvasN = new PdfCanvas(formN, pdfDocument);
            canvasN.setFillColorGray(1)
                   .rectangle(linkLocation2x.getX(), linkLocation2x.getY(), linkLocation2x.getWidth(), linkLocation2x.getHeight())
                   .fill();
            link2x.setNormalAppearance(formN.getPdfObject());
            link2x.setFlag(PdfAnnotation.PRINT);
            pdfDocument.getFirstPage().addAnnotation(link2x);

            Rectangle linkLocation3x = new Rectangle(150, 370, 90, 30);
            PdfAnnotation stamp1x = new PdfStampAnnotation(linkLocation3x)
                    .setStampName(new PdfName("Confidential"))
                    .setContents("Landscape").setColor(ColorConstants.BLACK.getColorValue());
            formN = new PdfFormXObject(linkLocation3x);
            canvasN = new PdfCanvas(formN, pdfDocument);
            canvasN.setFillColorGray(1)
                   .rectangle(linkLocation3x.getX(), linkLocation3x.getY(), linkLocation3x.getWidth(), linkLocation3x.getHeight())
                   .fill();
            stamp1x.setNormalAppearance(formN.getPdfObject());
            stamp1x.setFlag(PdfAnnotation.PRINT);
            pdfDocument.getFirstPage().addAnnotation(stamp1x);
        }
    }

}
