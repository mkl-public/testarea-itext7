using iText.IO.Image;
using iText.Kernel.Pdf;
using iText.Kernel.Pdf.Canvas;
using NUnit.Framework;

namespace iText7.Net_Playground.Content
{
    class AddImageWithMask
    {
        /// <summary>
        /// How to fill a shape with bitmap or mask a bitmap with shape in iText 7?
        /// https://stackoverflow.com/questions/67562635/how-to-fill-a-shape-with-bitmap-or-mask-a-bitmap-with-shape-in-itext-7
        /// 
        /// This test demonstrates how to _fill a shape with a bitmap_.
        /// </summary>
        [Test]
        public void testAddImageInShape()
        {
            using (PdfWriter writer = new PdfWriter(@"..\..\..\target\test-outputs\content\addImageInShapeNet.pdf"))
            using (PdfDocument pdfDoc = new PdfDocument(writer))
            {
                ImageData data = ImageDataFactory.Create(@"..\..\..\src\test\resources\mkl\testarea\itext7\annotate\Willi-1.jpg");

                PdfCanvas pdfCanvas = new PdfCanvas(pdfDoc.AddNewPage());
                pdfCanvas.SaveState()
                         .MoveTo(100, 100)
                         .LineTo(300, 200)
                         .LineTo(400, 400)
                         .LineTo(200, 300)
                         .ClosePath()
                         .EoClip()
                         .EndPath();
                pdfCanvas.AddImageAt(data, 100, 100, false);
                pdfCanvas.RestoreState();
            }
        }
    }
}
