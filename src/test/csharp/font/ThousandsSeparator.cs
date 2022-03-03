using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Text;
using iText.Kernel.Font;
using iText.Layout;
using iText.Layout.Element;
using NUnit.Framework;

namespace iText7.Net_Playground.Font
{
    class ThousandsSeparator
    {
        /// <summary>
        /// Wrong rendering of thousands separator with custom font in itext
        /// https://stackoverflow.com/questions/71322025/wrong-rendering-of-thousands-separator-with-custom-font-in-itext
        /// Brown-Bold.otf 
        /// https://www.swisstransfer.com/d/69d4f4c1-cf2b-4a51-a841-493237b3164f
        /// 
        /// Cannot reproduce the problem. I get "1.000,00 €" for the "fr-BE" culture,
        /// no matter whether it's iText 7.1.17, 7.2.0, or 7.2.1. 
        /// </summary>
        [Test]
        public void testLikeJohnValdevit()
        {
            CultureInfo.CurrentCulture = new CultureInfo("fr-BE");

            // Fonts
            var fontTexte = PdfFontFactory.CreateFont(@"..\..\..\src\test\resources\mkl\testarea\itext7\font\Brown-Bold.otf",
                PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);

            // Initialisation
            var ms = new MemoryStream();

            // Initialize PDF writer
            var writer = new iText.Kernel.Pdf.PdfWriter(ms);
            writer.SetCloseStream(false);

            // Initialize PDF document
            var pdfDoc = new iText.Kernel.Pdf.PdfDocument(writer);

            // Initialize document
            var document = new Document(pdfDoc, iText.Kernel.Geom.PageSize.A4)
                .SetFont(fontTexte).SetFontSize(10);

            // Values
            for (var i = 1000; i <= 2000; i += 100)
                document.Add(new Paragraph(i.ToString("C")));

            // Close document
            document.Close();

            ms.WriteTo(new FileStream(@"..\..\..\target\test-outputs\font\ThousandsSeparatorLikeJohnValdevitDotNet.pdf", FileMode.Create));
        }
    }
}
