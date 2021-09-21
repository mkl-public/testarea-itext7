using iText.Forms;
using iText.Forms.Fields;
using iText.Kernel.Pdf;
using NUnit.Framework;
using System.Collections.Generic;
using System.IO;

namespace iText7.Net_Playground.Form
{
    class SetFieldValue
    {
        /// <summary>
        /// iText7 text value is hidden
        /// https://stackoverflow.com/questions/69183319/itext7-text-value-is-hidden
        /// PdfTemplate.pdf
        /// https://github.com/Dusernajder/PdfTemplate
        /// 
        /// I cannot reproduce the OP's issue, immediately upon opening
        /// the PDF in Adobe Acrobat the new value is visible.
        /// 
        /// Even choosing a field which looks a bit different and has 
        /// Additional Actions configured, one gets the visible value.
        /// </summary>
        [Test]
        public void testSetDateInPdfTemplate()
        {
            Directory.CreateDirectory(@"..\..\..\target\test-outputs\form");
            using (PdfReader reader = new PdfReader(@"..\..\..\src\test\resources\mkl\testarea\itext7\form\PdfTemplate.pdf"))
            {
                reader.SetUnethicalReading(true);
                using (PdfDocument pdfDocument = new PdfDocument(reader, new PdfWriter(@"..\..\..\target\test-outputs\form\ModifiedPdfTemplate.pdf")))
                {
                    PdfAcroForm form = PdfAcroForm.GetAcroForm(pdfDocument, true);
                    IDictionary<string, PdfFormField> fields = form.GetFormFields();
                    fields["Tekstveld 52"].SetValue("DATE");
                    fields["Tekstveld 455"].SetValue("DATE");
                }
            }
        }
    }
}
