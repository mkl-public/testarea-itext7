using System;
using System.Collections.Generic;
using System.Text;
using iText.IO.Font.Constants;
using iText.Kernel.Colors;
using iText.Kernel.Font;
using iText.Kernel.Pdf;
using iText.Kernel.Pdf.Canvas;
using iText.Layout;
using iText.Layout.Element;
using NUnit.Framework;

namespace iText7.Net_Playground.Content
{
    class TextPosition
    {
        /// <summary>
        /// In iText 7 - a paragraph and a rectangle with same coordinates are not right overlapping
        /// https://stackoverflow.com/questions/71446903/in-itext-7-a-paragraph-and-a-rectangle-with-same-coordinates-are-not-right-ove
        /// 
        /// The OP forgot that leading is not automatically equal to font size.
        /// By setting the leading to the font size one gets the desired result.
        /// </summary>
        [Test]
        public void TextAndPositionLikeLandings()
        {
            var str = "ABCD1234";
            var fontSize = 32;
            var x = 100;
            var y = 700;
            var writer = new PdfWriter(@"..\..\..\target\test-outputs\content\TextAndPositionLikeLandings.pdf");
            var pdfdoc = new PdfDocument(writer);
            var doc = new Document(pdfdoc);
            var font = PdfFontFactory.CreateFont(StandardFonts.HELVETICA);
            var width = font.GetWidth(str, fontSize);
            var height = fontSize;

            // Draw rectangle
            var pdfPage = pdfdoc.AddNewPage();
            var pdfCanvas = new PdfCanvas(pdfPage);
            pdfCanvas.SetFillColor(ColorConstants.YELLOW);
            pdfCanvas.Rectangle(x, y, width, height);
            pdfCanvas.Fill();

            // Draw text
            var p = new Paragraph().Add(str).SetFont(font);
            p.SetFontSize(fontSize).SetFontColor(ColorConstants.BLACK);
            p.SetFixedPosition(x, y, width);
            p.SetFixedLeading(fontSize); // <-- added
            doc.Add(p);

            doc.Close();
            pdfdoc.Close();
            writer.Close();
        }
    }
}
