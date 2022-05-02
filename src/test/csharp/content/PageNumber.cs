using iText.Kernel.Geom;
using iText.Kernel.Pdf;
using iText.Layout;
using iText.Layout.Element;
using iText.Layout.Properties;
using NUnit.Framework;

namespace iText7.Net_Playground.Content
{
    class PageNumber
    {
        /// <summary>
        /// iText 7 .net page 2 missing? 7.2.2
        /// https://stackoverflow.com/questions/72083103/itext-7-net-page-2-missing-7-2-2
        /// 
        /// This test reduces the OP's code to the essentials to allow easily reproducing the issue.
        /// Furthermore, it provides an alternative way to retrieve the current page number that
        /// works as desired.
        /// </summary>
        [Test]
        public void UsingNumberOfPages()
        {
            using (PdfWriter pdfWriter = new PdfWriter(@"..\..\..\target\test-outputs\content\PageNumberFromNumberOfPages.pdf"))
            using (PdfDocument pdfDocument = new PdfDocument(pdfWriter))
            using (Document document = new Document(pdfDocument))
            {
                for (int i = 1; i < 5; i++)
                {
                    pdfDocument.AddNewPage(PageSize.LEGAL);

                    Paragraph paragraph = new Paragraph();
                    paragraph.Add(new Text($"This should be page {i}"));
                    paragraph.Add(new Text($" and PdfDocument.GetNumberOfPages is {pdfDocument.GetNumberOfPages()}"));
                    paragraph.Add(new Text($" and CurrentArea.GetPageNumber is {document.GetRenderer().GetCurrentArea().GetPageNumber()}"));
                    document.Add(paragraph);

                    document.Add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                }
            }
        }
    }
}
