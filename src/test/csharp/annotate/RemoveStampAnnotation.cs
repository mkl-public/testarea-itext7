using iText.Kernel.Colors;
using iText.Kernel.Geom;
using iText.Kernel.Pdf;
using iText.Kernel.Pdf.Annot;
using iText.Kernel.Pdf.Canvas;
using iText.Kernel.Pdf.Xobject;
using NUnit.Framework;
using System.Collections.Generic;
using System.IO;

namespace iText7.Net_Playground.Annotate
{
    class RemoveStampAnnotation
    {
        /// <summary>
        /// iText7 remove stamp
        /// https://stackoverflow.com/questions/67798068/itext7-remove-stamp
        /// 
        /// This test shows how to remove a stamp annotation as applied by the OP.
        /// </summary>
        [Test]
        public void testRemoveStampByShen()
        {
            Directory.CreateDirectory(@"..\..\..\target\test-outputs\annotate");
            using (PdfWriter pdfWriter = new PdfWriter(@"..\..\..\target\test-outputs\annotate\stampedLikeShen.pdf"))
            using (PdfDocument pdfDocument = new PdfDocument(pdfWriter))
            {
                PdfPage page = pdfDocument.AddNewPage();
                stampLikeShen(page);
            }

            using (PdfReader pdfReader = new PdfReader(@"..\..\..\target\test-outputs\annotate\stampedLikeShen.pdf"))
            using (PdfWriter pdfWriter = new PdfWriter(@"..\..\..\target\test-outputs\annotate\stampedLikeShen-Removed.pdf"))
            using (PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter))
            {
                for (int pageNr = 1; pageNr <= pdfDocument.GetNumberOfPages(); pageNr++)
                {
                    PdfPage page = pdfDocument.GetPage(pageNr);
                    IList<PdfAnnotation> annotations = page.GetAnnotations();
                    for (int i = annotations.Count - 1; i >= 0; i--)
                    {
                        PdfAnnotation annotation = annotations[i];
                        if (annotation is PdfStampAnnotation stamp)
                        {
                            if ("/StampRML" == stamp.GetStampName()?.ToString())
                            {
                                page.RemoveAnnotation(stamp);
                            }
                        }
                    }
                }
            }
        }

        void stampLikeShen(PdfPage page)
        {
            Rectangle stampRect = new Rectangle(200, 200, 100, 100);
            PdfStampAnnotation stampAnno = new PdfStampAnnotation(stampRect).SetStampName(new PdfName("StampRML"));
            PdfFormXObject stampObj = new PdfFormXObject(new Rectangle(100, 100));
            PdfCanvas pdfCanvas = new PdfCanvas(stampObj, page.GetDocument());
            pdfCanvas.SetStrokeColorRgb(1, 0, 0)
                     .SetFillColorRgb(0, 1, 0)
                     .Rectangle(0, 0, 100, 100)
                     .FillStroke()
                     .MoveTo(0, 0)
                     .LineTo(100, 100)
                     .MoveTo(0, 100)
                     .LineTo(100, 0)
                     .Stroke();
            stampAnno.SetNormalAppearance(stampObj.GetPdfObject());
            stampAnno.SetFlags(PdfAnnotation.PRINT);
            page.AddAnnotation(stampAnno);
        }
    }
}
