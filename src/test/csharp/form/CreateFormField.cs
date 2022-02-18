using iText.Forms;
using iText.Forms.Fields;
using iText.Kernel.Geom;
using iText.Kernel.Pdf;
using NUnit.Framework;
using System.Collections.Generic;
using System.IO;

namespace iText7.Net_Playground.Form
{
    class CreateFormField
    {
        /// <summary>
        /// PdfTextFormField values is not getting populated with iText 7.2.0
        /// https://stackoverflow.com/questions/71166730/pdftextformfield-values-is-not-getting-populated-with-itext-7-2-0
        /// 
        /// This test contains the OP's code. As no on-page area is given,
        /// the field is created as invisible field.
        /// </summary>
        [Test]
        public void testCreateFieldLikeUser14463446()
        {
            Directory.CreateDirectory(@"..\..\..\target\test-outputs\form");
            using (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(@"..\..\..\target\test-outputs\form\FieldLikeUser14463446.pdf")))
            {
                pdfDocument.AddNewPage();

                PdfAcroForm form = PdfAcroForm.GetAcroForm(pdfDocument, true);

                PdfTextFormField borrowerField = PdfFormField.CreateText(pdfDocument);

                borrowerField.SetFieldName("Borrowers_Name");
                borrowerField.SetValue("Name");
                form.AddField(borrowerField);
            }
        }

        /// <summary>
        /// PdfTextFormField values is not getting populated with iText 7.2.0
        /// https://stackoverflow.com/questions/71166730/pdftextformfield-values-is-not-getting-populated-with-itext-7-2-0
        /// 
        /// This test contains the OP's code but using a different CreateText
        /// overload that also accepts a rectangle. Now the field is created
        /// as visble field, showing the field value.
        /// </summary>
        [Test]
        public void testCreateFieldLikeUser14463446WithRectangle()
        {
            Directory.CreateDirectory(@"..\..\..\target\test-outputs\form");
            using (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(@"..\..\..\target\test-outputs\form\FieldLikeUser14463446WithRectangle.pdf")))
            {
                pdfDocument.AddNewPage();

                PdfAcroForm form = PdfAcroForm.GetAcroForm(pdfDocument, true);

                PdfTextFormField borrowerField = PdfFormField.CreateText(pdfDocument, new Rectangle(100, 600, 200, 20));

                borrowerField.SetFieldName("Borrowers_Name");
                borrowerField.SetValue("Name");
                form.AddField(borrowerField);
            }
        }
    }
}
