using iText.Kernel.Colors;
using iText.Kernel.Geom;
using iText.Kernel.Pdf;
using iText.Kernel.Pdf.Canvas.Parser;
using iText.Kernel.Pdf.Canvas.Parser.Data;
using iText.Kernel.Pdf.Canvas.Parser.Listener;
using NUnit.Framework;
using System;
using System.IO;
using System.Reflection;
using System.Text;

namespace iText7.Net_Playground.Extract
{
    class ExtractWithFontSize
    {
        /// <summary>
        /// Extract fontname, size, style from pdf with iText
        /// https://stackoverflow.com/questions/68133934/extract-fontname-size-style-from-pdf-with-itext
        /// test.pdf as testOndrums.pdf
        /// http://www.kornfelt.de/example/test.pdf
        /// 
        /// This is the OP's original code. His text extraction strategy
        /// <see cref="FontSizeSimpleTextExtractionStrategy"/> uses the
        /// font size as returned from the RenderInfo object.
        /// </summary>
        [Test]
        public void testExtractLikeOndrums()
        {
            string fileName = @"..\..\..\src\test\resources\mkl\testarea\itext7\extract\testOndrums.pdf";
            Directory.CreateDirectory(@"..\..\..\target\test-outputs\extract");

            StringBuilder allTextBuilder = new StringBuilder();
            using (PdfReader pdfReader = new PdfReader(fileName))
            using (PdfDocument pdfDocument = new PdfDocument(pdfReader))
            {
                for (int page = 1; page <= pdfDocument.GetNumberOfPages(); page++)
                {
                    allTextBuilder.AppendFormat("Page {0}\n----\n", page);
                    ITextExtractionStrategy strategy = new FontSizeSimpleTextExtractionStrategy();
                    string currentPageText = PdfTextExtractor.GetTextFromPage(pdfDocument.GetPage(page), strategy);
                    allTextBuilder.AppendFormat("{0}\n----\n", currentPageText);
                }
            }

            File.WriteAllText(@"..\..\..\target\test-outputs\extract\testOndrumsLikeOndrums.txt", allTextBuilder.ToString());
            Console.WriteLine("testOndrums.pdf like ondrums\n--------\n{0}", allTextBuilder.ToString());
        }

        /// <see cref="testExtractLikeOndrums"/>
        class FontSizeSimpleTextExtractionStrategy : SimpleTextExtractionStrategy
        {
            FieldInfo _textField = typeof(TextRenderInfo).GetField("text", BindingFlags.NonPublic | BindingFlags.Instance);
            public override void EventOccurred(IEventData data, EventType type)
            {
                if (type.Equals(EventType.RENDER_TEXT))
                {
                    TextRenderInfo renderInfo = (TextRenderInfo)data;
                    string fontName = renderInfo.GetFont()?.GetFontProgram()?.GetFontNames()?.GetFontName();
                    Color color = renderInfo.GetFillColor();
                    float size = renderInfo.GetFontSize();

                    if (fontName != null)
                    {
                        _textField.SetValue(renderInfo, "#Data|" + fontName + "|" + size.ToString() + "|" + ColorToString(color) + "|Data#" + renderInfo.GetText());
                    }
                }
                base.EventOccurred(data, type);
            }
        }

        /// <summary>
        /// Extract fontname, size, style from pdf with iText
        /// https://stackoverflow.com/questions/68133934/extract-fontname-size-style-from-pdf-with-itext
        /// test.pdf as testOndrums.pdf
        /// http://www.kornfelt.de/example/test.pdf
        /// 
        /// This is an improved version of the OP's original code. The text extraction 
        /// strategy <see cref="FontSizeSimpleTextExtractionStrategyImproved"/> applies
        /// the text matrix to the font size as returned from the RenderInfo object.
        /// </summary>
        [Test]
        public void testExtractLikeOndrumsImproved()
        {
            string fileName = @"..\..\..\src\test\resources\mkl\testarea\itext7\extract\testOndrums.pdf";
            Directory.CreateDirectory(@"..\..\..\target\test-outputs\extract");

            StringBuilder allTextBuilder = new StringBuilder();
            using (PdfReader pdfReader = new PdfReader(fileName))
            using (PdfDocument pdfDocument = new PdfDocument(pdfReader))
            {
                for (int page = 1; page <= pdfDocument.GetNumberOfPages(); page++)
                {
                    allTextBuilder.AppendFormat("Page {0}\n----\n", page);
                    ITextExtractionStrategy strategy = new FontSizeSimpleTextExtractionStrategyImproved();
                    string currentPageText = PdfTextExtractor.GetTextFromPage(pdfDocument.GetPage(page), strategy);
                    allTextBuilder.AppendFormat("{0}\n----\n", currentPageText);
                }
            }

            File.WriteAllText(@"..\..\..\target\test-outputs\extract\testOndrumsLikeOndrumsImproved.txt", allTextBuilder.ToString());
            Console.WriteLine("testOndrums.pdf like ondrums improved\n--------\n{0}", allTextBuilder.ToString());
        }

        /// <see cref="testExtractLikeOndrumsImproved"/>
        class FontSizeSimpleTextExtractionStrategyImproved : SimpleTextExtractionStrategy
        {
            FieldInfo _textField = typeof(TextRenderInfo).GetField("text", BindingFlags.NonPublic | BindingFlags.Instance);
            public override void EventOccurred(IEventData data, EventType type)
            {
                if (type.Equals(EventType.RENDER_TEXT))
                {
                    TextRenderInfo renderInfo = (TextRenderInfo)data;
                    string fontName = renderInfo.GetFont()?.GetFontProgram()?.GetFontNames()?.GetFontName();
                    Color color = renderInfo.GetFillColor();

                    float size = renderInfo.GetFontSize();
                    Vector sizeHighVector = new Vector(0, size, 0);
                    Matrix matrix = renderInfo.GetTextMatrix();
                    float sizeAdjusted = sizeHighVector.Cross(matrix).Length();

                    if (fontName != null)
                    {
                        _textField.SetValue(renderInfo, "#Data|" + fontName + "|" + sizeAdjusted.ToString() + "|" + ColorToString(color) + "|Data#" + renderInfo.GetText());
                    }
                }
                base.EventOccurred(data, type);
            }
        }

        public static string ColorToString(Color color)
        {
            float[] value = color?.GetColorValue();
            return value != null ? string.Join(',', value) : null;
        }
    }
}
