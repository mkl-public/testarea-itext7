package mkl.testarea.itext7.content;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;

/**
 * @author mkl
 */
public class RecognizeRotatedImage {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/48509488/itext-7-1-how-to-check-if-image-is-rotated">
     * itext 7.1 how to check if image is rotated
     * </a>
     * <p>
     * To discover the rotation of the original image, one needs to 
     * consider the EXIF image metadata like this.
     * </p>
     */
    @Test
    public void testOskar() throws IOException, ImageProcessingException, MetadataException {
        try (   InputStream resource = getClass().getResourceAsStream("Oskar.jpg");
                PdfDocument writerPdf = new PdfDocument(new PdfWriter(new File(RESULT_FOLDER, "RotatedOskar.pdf"))) ) {
            byte[] imageBytes = StreamUtil.inputStreamToArray(resource);

            Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(imageBytes));
            ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            int orientation = exifIFD0Directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);

            double angle = 0;
            switch (orientation)
            {
            case 1:
            case 2:
                angle = 0; break;
            case 3:
            case 4:
                angle = Math.PI; break;
            case 5:
            case 6:
                angle = - Math.PI / 2; break;
            case 7:
            case 8:
                angle = Math.PI / 2; break;
            }

            Document document = new Document(writerPdf);
            ImageData imgData = ImageDataFactory.create(imageBytes);
            Image image = new Image(imgData);
            image.setRotationAngle(angle);
            document.add(image);
        }
    }

}
