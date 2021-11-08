using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using iText.Forms;
using iText.Forms.Fields;
using iText.Kernel.Geom;
using iText.Kernel.Pdf;
using iText.Kernel.Pdf.Annot;
using NUnit.Framework;

namespace iText7.Net_Playground.Content
{
    /// <summary>
    /// How to copy everything from PDF page to PDF page?
    /// https://stackoverflow.com/questions/69878679/how-to-copy-everything-from-pdf-page-to-pdf-page
    /// 
    /// This test shows how to change the page sizes of a document but keeping
    /// everything else intact, in particular annotations.
    /// </summary>
    class ResizePages
    {
        [Test]
        public void ResizeForRobertSF()
        {
            var testFile = @"..\..\..\target\test-outputs\content\DocWithDifferentPageSizes.pdf";
            var resultFile = @"..\..\..\target\test-outputs\content\DocWithDifferentPageSizes-Resized.pdf";

            byte[] testData = createDocWithDifferentPageSizes();
            File.WriteAllBytes(testFile, testData);

            float resultWidth = 8.5f * 72;
            float resultHeight = 11f * 72;

            using (PdfDocument pdfDocument = new PdfDocument(new PdfReader(testFile), new PdfWriter(resultFile)))
            {
                for (int i = 1; i <= pdfDocument.GetNumberOfPages(); i++)
                {
                    var page = pdfDocument.GetPage(i);
                    var cropBox = page.GetCropBox();
                    var newCropBox = new Rectangle(cropBox.GetLeft() - (resultWidth - cropBox.GetWidth()) / 2,
                        cropBox.GetBottom() - (resultHeight - cropBox.GetHeight()) / 2, resultWidth, resultHeight);
                    var mediaBox = page.GetMediaBox();
                    var newMediaBox = Rectangle.GetCommonRectangle(mediaBox, newCropBox);
                    page.SetMediaBox(newMediaBox);
                    page.SetCropBox(newCropBox);
                }
            }
        }

        byte[] createDocWithDifferentPageSizes()
        {
            using (MemoryStream ms = new MemoryStream())
            {
                using (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(ms)))
                {
                    PdfAcroForm form = PdfAcroForm.GetAcroForm(pdfDocument, true);
                    Rectangle fieldRect = new Rectangle(2*72, 72);
                    for (int i = 0; i < 5; i++)
                    {
                        int xInch = 8 - i;
                        int yInch = 6 + i;
                        var page = pdfDocument.AddNewPage(new PageSize(xInch * 72, yInch * 72));
                        var field = PdfFormField.CreateText(pdfDocument);
                        field.SetFieldName($"Field Nr {i}");
                        field.AddKid(new PdfWidgetAnnotation(fieldRect));
                        form.AddField(field);
                    }
                }
                return ms.ToArray();
            }
        }
    }
}
