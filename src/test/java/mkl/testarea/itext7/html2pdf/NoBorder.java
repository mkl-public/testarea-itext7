package mkl.testarea.itext7.html2pdf;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.licensekey.LicenseKey;

/**
 * @author mklink
 */
public class NoBorder
{
    final static File RESULT_FOLDER = new File("target/test-outputs", "html2pdf");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/44936451/itext-7-overriding-default-margin-in-pdfhtml">
     * Itext 7 overriding default margin in pdfHtml
     * </a>
     * <p>
     * This test uses CSS <code>@page</code> to remove default margins. 
     * </p>
     */
    @Test
    public void testViaCss() throws IOException
    {
        LicenseKey.loadLicenseFile("itextkey-html2pdf_typography.xml");

        String resourceLoc = "src/test/resources/mkl/testarea/itext7/html2pdf/";

        InputStream htmlSource = new ByteArrayInputStream("<html><head><style>@page :first { margin: 0in 0in; }</style></head><body>Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test</body></html>".getBytes());

        try (   PdfWriter writer = new PdfWriter(new File(RESULT_FOLDER, "noBorderCss.pdf").getAbsolutePath());
                PdfDocument pdfDoc = new PdfDocument(writer);   )
        {
            pdfDoc.setTagged();
            pdfDoc.setDefaultPageSize(PageSize.A4);

            ConverterProperties converterProperties = new ConverterProperties();
            converterProperties.setBaseUri(resourceLoc);

            HtmlConverter.convertToDocument(htmlSource, pdfDoc, converterProperties);
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/44936451/itext-7-overriding-default-margin-in-pdfhtml">
     * Itext 7 overriding default margin in pdfHtml
     * </a>
     * <p>
     * This test uses {@link HtmlConverter#convertToElements(InputStream)} and a
     * self-created {@link Document} with zero margins to remove default margins. 
     * </p>
     */
    @Test
    public void testViaElements() throws IOException
    {
        LicenseKey.loadLicenseFile("itextkey-html2pdf_typography.xml");

        String resourceLoc = "src/test/resources/mkl/testarea/itext7/html2pdf/";

        PageSize pageSize =  PageSize.A4;

        try (   PdfWriter writer = new PdfWriter(new File(RESULT_FOLDER, "noBorder.pdf").getAbsolutePath());
                PdfDocument pdfDoc = new PdfDocument(writer);
                Document document = new Document(pdfDoc);   )
        {
            pdfDoc.setTagged();
            pdfDoc.setDefaultPageSize(pageSize);
     
            document.setMargins(0, 0, 0, 0);
            
            ConverterProperties converterProperties = new ConverterProperties();
            converterProperties.setBaseUri(resourceLoc);

            try ( InputStream htmlSource = getClass().getResourceAsStream("responsive.html")    )
            {
                List<IElement> elements = HtmlConverter.convertToElements(htmlSource, converterProperties);
                for (IElement element : elements)
                {
                    if (element instanceof IBlockElement)
                        document.add((IBlockElement) element);
                    else
                    {
                        System.out.println("XXX");
                    }
                }
            }
        }
    }
}
