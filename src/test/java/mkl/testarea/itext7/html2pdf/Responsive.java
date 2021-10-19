package mkl.testarea.itext7.html2pdf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.licensekey.LicenseKey;
import com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription;
import com.itextpdf.styledxmlparser.css.media.MediaType;
import com.itextpdf.styledxmlparser.css.util.CssDimensionParsingUtils;

/**
 * @author mklink
 */
public class Responsive
{
    final static File RESULT_FOLDER = new File("target/test-outputs", "html2pdf");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
    }

    @Test
    public void testResponsiveDesign() throws IOException
    {
        LicenseKey.loadLicenseFile("itextkey-html2pdf_typography.xml");

        String resourceFolder = "src/test/resources/mkl/testarea/itext7/html2pdf/";
        PageSize[] pageSizes = {
                PageSize.A4.rotate(),
                new PageSize(720, PageSize.A4.getHeight()),
                new PageSize(PageSize.A5.getWidth(), PageSize.A4.getHeight())
        };

        for (int i = 0; i < pageSizes.length; i++) {
            float width = CssDimensionParsingUtils.parseAbsoluteLength("" + pageSizes[i].getWidth());
            File dest = new File(RESULT_FOLDER, String.format("responsive_%s.pdf", width));
            try ( InputStream htmlSource = getClass().getResourceAsStream("responsive.html")    )
            {
                parseMedia(htmlSource, dest, resourceFolder, pageSizes[i], width);
            }
        }
    }

    public void parseMedia(InputStream htmlSource, File pdfDest, String resourceLoc, PageSize pageSize, float screenWidth) throws IOException {
        PdfWriter writer = new PdfWriter(pdfDest.getAbsolutePath());
        PdfDocument pdfDoc = new PdfDocument(writer);
 
        //Set the result to be tagged
        pdfDoc.setTagged();
        pdfDoc.setDefaultPageSize(pageSize);
 
        ConverterProperties converterProperties = new ConverterProperties();
 
        //Set media device description details
        MediaDeviceDescription mediaDescription = new MediaDeviceDescription(MediaType.SCREEN);
        mediaDescription.setWidth(screenWidth);
        converterProperties.setMediaDeviceDescription(mediaDescription);
 
        FontProvider fp = new FontProvider();
        fp.addStandardPdfFonts();
        //Register external font directory
        fp.addDirectory(resourceLoc);
 
        converterProperties.setFontProvider(fp);
        converterProperties.setBaseUri(resourceLoc);
 
        //Create acroforms from text and button input fields
        //converterProperties.setCreateAcroForm(true);
 
        HtmlConverter.convertToPdf(htmlSource, pdfDoc, converterProperties);
        pdfDoc.close();
    }
}
