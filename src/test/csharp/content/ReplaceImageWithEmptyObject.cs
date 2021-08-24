using iText.Kernel.Geom;
using iText.Kernel.Pdf;
using iText.Kernel.Pdf.Xobject;
using NUnit.Framework;
using System.Collections.Generic;

namespace iText7.Net_Playground.Content
{
    class ReplaceImageWithEmptyObject
    {
        /// <summary>
        /// How to copy OCR text data to a new pdf Itext 7 C#
        /// https://stackoverflow.com/questions/68691905/how-to-copy-ocr-text-data-to-a-new-pdf-itext-7-c-sharp
        /// test.pdf as testVZ.pdf
        /// https://drive.google.com/file/d/1k3Ghbw5uDH-ziGQw7PBtvbtj-BcACsf4/view?usp=sharing
        /// 
        /// This test shows how to remove the images by replacing them with empty
        /// form XObjects.
        /// </summary>
        [Test]
        public void testReplaceForVZ()
        {
            using (PdfReader pdfReader = new PdfReader(@"..\..\..\src\test\resources\mkl\testarea\itext7\content\testVZ.pdf"))
            using (PdfWriter pdfWriter = new PdfWriter(@"..\..\..\target\test-outputs\content\testVZ-imagesReplaced.pdf"))
            using (PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter))
            {
                PdfFormXObject replacement = new PdfFormXObject(new Rectangle(1, 1));
                for (int pageNr = 1; pageNr <= pdfDocument.GetNumberOfPages(); pageNr++)
                {
                    PdfPage pdfPage = pdfDocument.GetPage(pageNr);
                    PdfResources pdfResources = pdfPage.GetResources();
                    replaceImages(pdfResources, replacement.GetPdfObject());
                }
            }
        }

        /// <summary>
        /// Used by <see cref="testReplaceForVZ"/>; beware, the resources argument will be
        /// inconsistent and should not be used further after calling this method.
        /// </summary>
        void replaceImages(PdfResources pdfResources, PdfObject replacement)
        {
            PdfDictionary xobjects = pdfResources.GetPdfObject().GetAsDictionary(PdfName.XObject);
            if (xobjects == null)
                return;
            ISet<PdfName> toReplace = new HashSet<PdfName>();
            foreach (KeyValuePair<PdfName, PdfObject> entry in xobjects.EntrySet())
            {
                PdfObject pdfObject = entry.Value;
                if (pdfObject is PdfIndirectReference reference)
                    pdfObject = reference.GetRefersTo();
                if (pdfObject is PdfStream pdfStream && PdfName.Image.Equals(pdfStream.GetAsName(PdfName.Subtype)))
                {
                    toReplace.Add(entry.Key);
                }
            }
            foreach (PdfName name in toReplace)
            {
                xobjects.Put(name, replacement);
            }
        }
    }
}
