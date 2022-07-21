using iText.Kernel.Geom;
using iText.Kernel.Pdf;
using iText.Kernel.Pdf.Canvas;
using iText.Kernel.Pdf.Xobject;
using NUnit.Framework;

namespace iText7.Net_Playground.Content
{
    class Knockout
    {
        /// <summary>
        /// How to knockout a pdf file using iText7 C#
        /// https://stackoverflow.com/questions/73030457/how-to-knockout-a-pdf-file-using-itext7-c-sharp
        /// to_knockout.pdf
        /// https://1drv.ms/b/s!AqxJcIfHZ18UiHlqmpXn1YA8HLR8?e=Fcd7n4
        /// 
        /// This test executes the OP's code. The result actually is to be expected for a knockout group.
        /// </summary>
        [Test]
        public void testKnockoutLikeZhusp()
        {
            var sourceFPath = @"..\..\..\src\test\resources\mkl\testarea\itext7\content\to_knockout.pdf ";
            var targetFPath = @"..\..\..\target\test-outputs\content\KnockoutLikeZhusp.pdf";
            using (PdfDocument origPdf = new PdfDocument(new PdfReader(sourceFPath)))
            {
                var currPage = origPdf.GetPage(1);
                using (PdfDocument destPdf = new PdfDocument(new PdfWriter(targetFPath)))
                {
                    var page = destPdf.AddNewPage(new PageSize(100 * 72f / 25.4f, 125 * 72f / 25.4f));
                    PdfCanvas canvas = new PdfCanvas(page);
                    PdfFormXObject xObject2 = currPage.CopyAsFormXObject(destPdf);
                    PdfTransparencyGroup transGroup = new PdfTransparencyGroup();
                    transGroup.SetKnockout(true);
                    transGroup.SetIsolated(false);
                    xObject2.SetGroup(transGroup);
                    canvas.AddXObjectAt(xObject2, 0, 0);
                }
            }
        }
    }
}
