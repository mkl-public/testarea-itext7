package mkl.testarea.itext7.form;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;

/**
 * @author mkl
 */
public class SetButtonImage {
    final static File RESULT_FOLDER = new File("target/test-outputs", "form");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/53082989/itext7-pdfbuttonformfield-setimage-method-does-not-work-on-a-signed-pdf">
     * itext7 PdfButtonFormField setImage method does not work on a signed pdf
     * </a>
     * <p>
     * Helper method creating a PDF with a button like the OP did it.
     * </p>
     */
    @Test
    public void createButtonPdf() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("/mkl/testarea/itext7/content/test.pdf");
                PdfReader reader = new PdfReader(resource);
                OutputStream outputStream = new FileOutputStream(new File(RESULT_FOLDER, "Button.pdf"));
                PdfDocument pdf = new PdfDocument(reader, new PdfWriter(outputStream)) ) {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);
            PdfButtonFormField button = PdfFormField.createPushButton(pdf, new Rectangle(107, 654, 160, 50), "Button", "Image");

            form.addField(button, pdf.getFirstPage());

            String image = "src/test/resources/mkl/testarea/itext7/form/2x2colored.png";
            button.setImage(image);
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/53082989/itext7-pdfbuttonformfield-setimage-method-does-not-work-on-a-signed-pdf">
     * itext7 PdfButtonFormField setImage method does not work on a signed pdf
     * </a>
     * <br/>
     * <a href="https://jumpshare.com/v/Ks5yM1zJd0MXwz66HQgp?b=SSPykvmwfTGxedSkQnNg">
     * acro_sample_empty_fields.pdf
     * </a>
     * <p>
     * Execute the OP's code for his non-signed sample. The button shows the
     * image on all viewers.
     * </p>
     */
    @Test
    public void testLikeGautamAnandAcroSampleEmptyFields() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("acro_sample_empty_fields.pdf");
                PdfReader reader = new PdfReader(resource);
                OutputStream outputStream = new FileOutputStream(new File(RESULT_FOLDER, "acro_sample_empty_fields-image.pdf"));
                PdfDocument document = new PdfDocument(reader,
                        new PdfWriter(outputStream),
                        new StampingProperties().useAppendMode());) {
            setLikeGautamAnand(document, "IMG_1");
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/53082989/itext7-pdfbuttonformfield-setimage-method-does-not-work-on-a-signed-pdf">
     * itext7 PdfButtonFormField setImage method does not work on a signed pdf
     * </a>
     * <br/>
     * <a href="https://jumpshare.com/v/lWdAAcRbg4LJQnuVVD9I?b=SSPykvmwfTGxedSkQnNg">
     * 1540982441_313554925_acro_sample_empty_fields_signedFinal.pdf
     * </a>
     * <p>
     * Execute the OP's code for his signed sample. The button is blank in
     * Adobe Reader but shows the image on all other viewers.
     * </p>
     */
    @Test
    public void testLikeGautamAnand1540982441_313554925AcroSampleEmptyFieldsSignedFinal() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("1540982441_313554925_acro_sample_empty_fields_signedFinal.pdf");
                PdfReader reader = new PdfReader(resource);
                OutputStream outputStream = new FileOutputStream(new File(RESULT_FOLDER, "1540982441_313554925_acro_sample_empty_fields_signedFinal-image.pdf"));
                PdfDocument document = new PdfDocument(reader,
                        new PdfWriter(outputStream),
                        new StampingProperties().useAppendMode());) {
            setLikeGautamAnand(document, "IMG_1");
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/53082989/itext7-pdfbuttonformfield-setimage-method-does-not-work-on-a-signed-pdf">
     * itext7 PdfButtonFormField setImage method does not work on a signed pdf
     * </a>
     * <p>
     * Execute the OP's code for our non-signed sample. The button shows the
     * image on all viewers.
     * </p>
     */
    @Test
    public void testLikeGautamAnandButton() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("Button.pdf");
                PdfReader reader = new PdfReader(resource);
                OutputStream outputStream = new FileOutputStream(new File(RESULT_FOLDER, "Button-image.pdf"));
                PdfDocument document = new PdfDocument(reader,
                        new PdfWriter(outputStream),
                        new StampingProperties().useAppendMode());) {
            setLikeGautamAnand(document, "Button");
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/53082989/itext7-pdfbuttonformfield-setimage-method-does-not-work-on-a-signed-pdf">
     * itext7 PdfButtonFormField setImage method does not work on a signed pdf
     * </a>
     * <p>
     * Execute the OP's code for our signed sample. The button is unchanged in
     * Adobe Reader but shows the new image on all other viewers.
     * </p>
     */
    @Test
    public void testLikeGautamAnandButtonSigned() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("Button-signed.pdf");
                PdfReader reader = new PdfReader(resource);
                OutputStream outputStream = new FileOutputStream(new File(RESULT_FOLDER, "Button-signed-image.pdf"));
                PdfDocument document = new PdfDocument(reader,
                        new PdfWriter(outputStream),
                        new StampingProperties().useAppendMode());) {
            setLikeGautamAnand(document, "Button");
        }
    }

    /**
     * The OP's code for setting the new button image.
     * 
     * @see #testLikeGautamAnandAcroSampleEmptyFields()
     * @see #testLikeGautamAnand1540982441_313554925AcroSampleEmptyFieldsSignedFinal()
     * @see #testLikeGautamAnandButton()
     * @see #testLikeGautamAnandButtonSigned()
     */
    void setLikeGautamAnand(PdfDocument document, String buttonName) throws IOException {
        PdfAcroForm form = PdfAcroForm.getAcroForm(document, true);
        Map<String, PdfFormField> fields = form.getFormFields();

        String resource = "src/test/resources/mkl/testarea/itext7/annotate/Willi-1.jpg";
        ((PdfButtonFormField)fields.get(buttonName)).setImage(resource);
    }

    /**
     * <a href="https://stackoverflow.com/questions/53082989/itext7-pdfbuttonformfield-setimage-method-does-not-work-on-a-signed-pdf">
     * itext7 PdfButtonFormField setImage method does not work on a signed pdf
     * </a>
     * <br/>
     * <a href="https://jumpshare.com/v/Ks5yM1zJd0MXwz66HQgp?b=SSPykvmwfTGxedSkQnNg">
     * acro_sample_empty_fields.pdf
     * </a>
     * <p>
     * Execute a minimalistic image setting routine (adding button characteristics)
     * for the OP's non-signed sample. The button shows the image on all viewers.
     * </p>
     */
    @Test
    public void testMinimallyAcroSampleEmptyFields() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("acro_sample_empty_fields.pdf");
                PdfReader reader = new PdfReader(resource);
                OutputStream outputStream = new FileOutputStream(new File(RESULT_FOLDER, "acro_sample_empty_fields-image-minimally.pdf"));
                PdfDocument document = new PdfDocument(reader,
                        new PdfWriter(outputStream),
                        new StampingProperties().useAppendMode());) {
            setMinimally(document, "IMG_1", false);
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/53082989/itext7-pdfbuttonformfield-setimage-method-does-not-work-on-a-signed-pdf">
     * itext7 PdfButtonFormField setImage method does not work on a signed pdf
     * </a>
     * <br/>
     * <a href="https://jumpshare.com/v/lWdAAcRbg4LJQnuVVD9I?b=SSPykvmwfTGxedSkQnNg">
     * 1540982441_313554925_acro_sample_empty_fields_signedFinal.pdf
     * </a>
     * <p>
     * Execute a minimalistic image setting routine (removing all characteristics)
     * for the OP's signed sample. The button shows the image on all viewers.
     * </p>
     */
    @Test
    public void testMinimallyRemoveCharacteristics1540982441_313554925AcroSampleEmptyFieldsSignedFinal() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("1540982441_313554925_acro_sample_empty_fields_signedFinal.pdf");
                PdfReader reader = new PdfReader(resource);
                OutputStream outputStream = new FileOutputStream(new File(RESULT_FOLDER, "1540982441_313554925_acro_sample_empty_fields_signedFinal-image-minimally-noChar.pdf"));
                PdfDocument document = new PdfDocument(reader,
                        new PdfWriter(outputStream),
                        new StampingProperties().useAppendMode());) {
            setMinimally(document, "IMG_1", true);
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/53082989/itext7-pdfbuttonformfield-setimage-method-does-not-work-on-a-signed-pdf">
     * itext7 PdfButtonFormField setImage method does not work on a signed pdf
     * </a>
     * <br/>
     * <a href="https://jumpshare.com/v/lWdAAcRbg4LJQnuVVD9I?b=SSPykvmwfTGxedSkQnNg">
     * 1540982441_313554925_acro_sample_empty_fields_signedFinal.pdf
     * </a>
     * <p>
     * Execute a minimalistic image setting routine (adding button characteristics)
     * for the OP's signed sample. The button shows the image on all viewers, but
     * a bit differently on Adobe Reader.
     * </p>
     */
    @Test
    public void testMinimallyAddIcon1540982441_313554925AcroSampleEmptyFieldsSignedFinal() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("1540982441_313554925_acro_sample_empty_fields_signedFinal.pdf");
                PdfReader reader = new PdfReader(resource);
                OutputStream outputStream = new FileOutputStream(new File(RESULT_FOLDER, "1540982441_313554925_acro_sample_empty_fields_signedFinal-image-minimally-icon.pdf"));
                PdfDocument document = new PdfDocument(reader,
                        new PdfWriter(outputStream),
                        new StampingProperties().useAppendMode());) {
            setMinimally(document, "IMG_1", false);
        }
    }

    /**
     * Minimalistic code setting the new button image and at the same
     * time either completely removing appearance characteristics or
     * adding some button specific ones.
     * 
     * @see #testMinimallyAcroSampleEmptyFields()
     * @see #testMinimallyRemoveCharacteristics1540982441_313554925AcroSampleEmptyFieldsSignedFinal()
     * @see #testMinimallyAddIcon1540982441_313554925AcroSampleEmptyFieldsSignedFinal()
     */
    void setMinimally(PdfDocument document, String buttonName, boolean removeCharacteristics) throws MalformedURLException {
        PdfAcroForm form = PdfAcroForm.getAcroForm(document, true);
        PdfFormField field = form.getField(buttonName);
        PdfFormXObject icon = null;
        for (PdfWidgetAnnotation widget : field.getWidgets()) {
            PdfStream appearance = (PdfStream) widget.getNormalAppearanceObject();
            icon = setMinimally(document, new PdfFormXObject(appearance));
        }
        if (icon != null) {
            field.setModified();
            if (removeCharacteristics) {
                field.getPdfObject().remove(PdfName.MK);
            } else {
                PdfDictionary characteristics = field.getPdfObject().getAsDictionary(PdfName.MK);
                if (characteristics != null) {
                    characteristics.setModified();
                    characteristics.put(PdfName.I, icon.getPdfObject());
                    characteristics.put(PdfName.TP, new PdfNumber(1));
                }
            }
        }
    }

    /**
     * helper of {@link #setMinimally(PdfDocument, String, boolean)}
     */
    PdfFormXObject setMinimally(PdfDocument document, PdfFormXObject formXObject) throws MalformedURLException {
        PdfFormXObject result = null;
        PdfResources resources = formXObject.getResources();
        Set<PdfName> names = resources.getResourceNames(PdfName.XObject);
        for (PdfName  name : names) {
            PdfFormXObject form = resources.getForm(name);
            if (form != null) {
                PdfFormXObject subResult = setMinimally(document, form);
                if (subResult != null)
                    result = subResult;
            } else {
                PdfImageXObject image = resources.getImage(name);
                if (image != null) {
                    ImageData data = ImageDataFactory.create("src/test/resources/mkl/testarea/itext7/annotate/Willi-1.jpg");
                    PdfImageXObject replacement = new PdfImageXObject(data);
                    resources.getPdfObject().getAsDictionary(PdfName.XObject).put(name, replacement.getPdfObject().makeIndirect(document));
                    resources.getPdfObject().getAsDictionary(PdfName.XObject).setModified();
                    resources.getPdfObject().setModified();
                    formXObject.setModified();
                    result = formXObject;
                }
            }
        }
        return result;
    }

    /**
     * <a href="https://stackoverflow.com/questions/53082989/itext7-pdfbuttonformfield-setimage-method-does-not-work-on-a-signed-pdf">
     * itext7 PdfButtonFormField setImage method does not work on a signed pdf
     * </a>
     * <br/>
     * <a href="https://jumpshare.com/v/Ks5yM1zJd0MXwz66HQgp?b=SSPykvmwfTGxedSkQnNg">
     * acro_sample_empty_fields.pdf
     * </a>
     * <p>
     * Execute the OP's code plus appearance characteristics removal for his
     * non-signed sample. The button shows the image on all viewers.
     * </p>
     */
    @Test
    public void testLikeGautamAnandImprovedAcroSampleEmptyFields() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("acro_sample_empty_fields.pdf");
                PdfReader reader = new PdfReader(resource);
                OutputStream outputStream = new FileOutputStream(new File(RESULT_FOLDER, "acro_sample_empty_fields-image-improved.pdf"));
                PdfDocument document = new PdfDocument(reader,
                        new PdfWriter(outputStream),
                        new StampingProperties().useAppendMode());) {
            setLikeGautamAnandImproved(document, "IMG_1");
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/53082989/itext7-pdfbuttonformfield-setimage-method-does-not-work-on-a-signed-pdf">
     * itext7 PdfButtonFormField setImage method does not work on a signed pdf
     * </a>
     * <br/>
     * <a href="https://jumpshare.com/v/lWdAAcRbg4LJQnuVVD9I?b=SSPykvmwfTGxedSkQnNg">
     * 1540982441_313554925_acro_sample_empty_fields_signedFinal.pdf
     * </a>
     * <p>
     * Execute the OP's code plus appearance characteristics removal for his
     * signed sample. The button shows the image on all viewers.
     * </p>
     */
    @Test
    public void testLikeGautamAnandImproved1540982441_313554925AcroSampleEmptyFieldsSignedFinal() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("1540982441_313554925_acro_sample_empty_fields_signedFinal.pdf");
                PdfReader reader = new PdfReader(resource);
                OutputStream outputStream = new FileOutputStream(new File(RESULT_FOLDER, "1540982441_313554925_acro_sample_empty_fields_signedFinal-image-improved.pdf"));
                PdfDocument document = new PdfDocument(reader,
                        new PdfWriter(outputStream),
                        new StampingProperties().useAppendMode());) {
            setLikeGautamAnandImproved(document, "IMG_1");
        }
    }

    /**
     * The OP's code for setting the new button image plus appearance characteristics removal.
     * 
     * @see #testLikeGautamAnandImprovedAcroSampleEmptyFields()
     * @see #testLikeGautamAnandImproved1540982441_313554925AcroSampleEmptyFieldsSignedFinal()
     */
    void setLikeGautamAnandImproved(PdfDocument document, String buttonName) throws IOException {
        PdfAcroForm form = PdfAcroForm.getAcroForm(document, true);
        Map<String, PdfFormField> fields = form.getFormFields();

        String resource = "src/test/resources/mkl/testarea/itext7/annotate/Willi-1.jpg";
        PdfButtonFormField button = (PdfButtonFormField)fields.get(buttonName);
        button.setImage(resource);
        if (button.getPdfObject().containsKey(PdfName.MK)) {
            button.setModified();
            button.getPdfObject().remove(PdfName.MK);
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/53082989/itext7-pdfbuttonformfield-setimage-method-does-not-work-on-a-signed-pdf">
     * itext7 PdfButtonFormField setImage method does not work on a signed pdf
     * </a>
     * <br/>
     * <a href="https://jumpshare.com/v/Ks5yM1zJd0MXwz66HQgp?b=SSPykvmwfTGxedSkQnNg">
     * acro_sample_empty_fields.pdf
     * </a>
     * <p>
     * Execute the OP's code plus appearance characteristics additions for his
     * non-signed sample. The button shows the image on all viewers.
     * </p>
     */
    @Test
    public void testLikeGautamAnandImproved2AcroSampleEmptyFields() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("acro_sample_empty_fields.pdf");
                PdfReader reader = new PdfReader(resource);
                OutputStream outputStream = new FileOutputStream(new File(RESULT_FOLDER, "acro_sample_empty_fields-image-improved2.pdf"));
                PdfDocument document = new PdfDocument(reader,
                        new PdfWriter(outputStream),
                        new StampingProperties().useAppendMode());) {
            setLikeGautamAnandImproved2(document, "IMG_1");
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/53082989/itext7-pdfbuttonformfield-setimage-method-does-not-work-on-a-signed-pdf">
     * itext7 PdfButtonFormField setImage method does not work on a signed pdf
     * </a>
     * <br/>
     * <a href="https://jumpshare.com/v/lWdAAcRbg4LJQnuVVD9I?b=SSPykvmwfTGxedSkQnNg">
     * 1540982441_313554925_acro_sample_empty_fields_signedFinal.pdf
     * </a>
     * <p>
     * Execute the OP's code plus appearance characteristics additions for his
     * signed sample. The button shows the image on all viewers, but
     * a bit differently on Adobe Reader.
     * </p>
     */
    @Test
    public void testLikeGautamAnandImproved21540982441_313554925AcroSampleEmptyFieldsSignedFinal() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("1540982441_313554925_acro_sample_empty_fields_signedFinal.pdf");
                PdfReader reader = new PdfReader(resource);
                OutputStream outputStream = new FileOutputStream(new File(RESULT_FOLDER, "1540982441_313554925_acro_sample_empty_fields_signedFinal-image-improved2.pdf"));
                PdfDocument document = new PdfDocument(reader,
                        new PdfWriter(outputStream),
                        new StampingProperties().useAppendMode());) {
            setLikeGautamAnandImproved2(document, "IMG_1");
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/53082989/itext7-pdfbuttonformfield-setimage-method-does-not-work-on-a-signed-pdf">
     * itext7 PdfButtonFormField setImage method does not work on a signed pdf
     * </a>
     * <p>
     * Execute the OP's code plus appearance characteristics additions for our
     * non-signed sample. The button shows the image on all viewers.
     * </p>
     */
    @Test
    public void testLikeGautamAnandImproved2Button() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("Button.pdf");
                PdfReader reader = new PdfReader(resource);
                OutputStream outputStream = new FileOutputStream(new File(RESULT_FOLDER, "Button-image-improved2.pdf"));
                PdfDocument document = new PdfDocument(reader,
                        new PdfWriter(outputStream),
                        new StampingProperties().useAppendMode());) {
            setLikeGautamAnandImproved2(document, "Button");
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/53082989/itext7-pdfbuttonformfield-setimage-method-does-not-work-on-a-signed-pdf">
     * itext7 PdfButtonFormField setImage method does not work on a signed pdf
     * </a>
     * <p>
     * Execute the OP's code plus appearance characteristics additions for our
     * signed sample. The button shows the image on all viewers, but
     * a bit differently on Adobe Reader.
     * </p>
     */
    @Test
    public void testLikeGautamAnandImproved2ButtonSigned() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("Button-signed.pdf");
                PdfReader reader = new PdfReader(resource);
                OutputStream outputStream = new FileOutputStream(new File(RESULT_FOLDER, "Button-signed-image-improved2.pdf"));
                PdfDocument document = new PdfDocument(reader,
                        new PdfWriter(outputStream),
                        new StampingProperties().useAppendMode());) {
            setLikeGautamAnandImproved2(document, "Button");
        }
    }

    /**
     * The OP's code for setting the new button image plus appearance characteristics additions.
     * 
     * @see #testLikeGautamAnandImproved2AcroSampleEmptyFields()
     * @see #testLikeGautamAnandImproved21540982441_313554925AcroSampleEmptyFieldsSignedFinal()
     * @see #testLikeGautamAnandImproved2Button()
     * @see #testLikeGautamAnandImproved2ButtonSigned()
     */
    void setLikeGautamAnandImproved2(PdfDocument document, String buttonName) throws IOException {
        PdfAcroForm form = PdfAcroForm.getAcroForm(document, true);
        Map<String, PdfFormField> fields = form.getFormFields();

        String resource = "src/test/resources/mkl/testarea/itext7/annotate/Willi-1.jpg";
        PdfButtonFormField button = (PdfButtonFormField)fields.get(buttonName);
        button.setImage(resource);
        PdfWidgetAnnotation widget = button.getWidgets().get(0);
        PdfDictionary characteristics = widget.getAppearanceCharacteristics();
        if (characteristics != null) {
            characteristics.setModified();
            characteristics.put(PdfName.I, widget.getNormalAppearanceObject());
            characteristics.put(PdfName.TP, new PdfNumber(1));
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/54107835/itext-7-set-image-to-button-that-appears-multiple-times">
     * iText 7 : Set image to button that appears multiple times
     * </a>
     * <br/>
     * <a href="https://drive.google.com/file/d/1k1hctPecvwMQ-2eX1ieoRDEP5ktBrYWb/view?usp=sharing">
     * itext_multiple_images.pdf
     * </a>
     * <p>
     * Indeed, setting the image of a multi-widget pushbutton field fails.
     * Furthermore, version 7.1.4 used to throw an exception during form
     * flattening which already is fixed in the 7.1.5-SNAPSHOT as of now.
     * </p>
     * <p>
     * The cause of the error is the lacking support for form fields with
     * multiple widgets of {@link PdfFormField#regenerateField()}.
     * </p>
     */
    @Test
    public void testSetImageToButtonWithManyVisualizations() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("itext_multiple_images.pdf");
                PdfReader reader = new PdfReader(resource);
                OutputStream outputStream = new FileOutputStream(new File(RESULT_FOLDER, "itext_multiple_images-with-image.pdf"));) {
            PdfDocument pdfDocument = new PdfDocument(reader, new PdfWriter(outputStream));
            PdfAcroForm acroForm = PdfAcroForm.getAcroForm(pdfDocument, false);

            PdfButtonFormField button = (PdfButtonFormField) acroForm.getField("image");
            button.setImage("src\\test\\resources\\mkl\\testarea\\itext7\\form\\2x2colored.png");

            acroForm.flattenFields();

            pdfDocument.close();
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/54107835/itext-7-set-image-to-button-that-appears-multiple-times">
     * iText 7 : Set image to button that appears multiple times
     * </a>
     * <br/>
     * <a href="https://drive.google.com/file/d/1k1hctPecvwMQ-2eX1ieoRDEP5ktBrYWb/view?usp=sharing">
     * itext_multiple_images.pdf
     * </a>
     * <p>
     * This is a work-around for the effectively misbehaving setImage
     * call in {@link #testSetImageToButtonWithManyVisualizations()};
     * it is replaced by code iterating over the widgets and adds the
     * image as normal appearance to each of them. 
     * </p>
     */
    @Test
    public void testSetImageToButtonWithManyVisualizationsWorkAround() throws IOException {
        try (   InputStream resource = getClass().getResourceAsStream("itext_multiple_images.pdf");
                PdfReader reader = new PdfReader(resource);
                OutputStream outputStream = new FileOutputStream(new File(RESULT_FOLDER, "itext_multiple_images-with-image-workaround.pdf"));) {
            PdfDocument pdfDocument = new PdfDocument(reader, new PdfWriter(outputStream));
            PdfAcroForm acroForm = PdfAcroForm.getAcroForm(pdfDocument, false);

            PdfButtonFormField button = (PdfButtonFormField) acroForm.getField("image");

            ImageData img = ImageDataFactory.create("src\\test\\resources\\mkl\\testarea\\itext7\\form\\2x2colored.png");
            PdfImageXObject imgXObj = new PdfImageXObject(img);
            List<PdfWidgetAnnotation> widgets = button.getWidgets();
            for (PdfWidgetAnnotation widget : widgets) {
                Rectangle rectangle = widget.getRectangle().toRectangle();
                PdfFormXObject xObject = new PdfFormXObject(rectangle);
                PdfCanvas canvas = new PdfCanvas(xObject, pdfDocument);
                canvas.addXObjectWithTransformationMatrix(imgXObj, rectangle.getWidth(), 0, 0, rectangle.getHeight(), rectangle.getLeft(), rectangle.getBottom());
                widget.setNormalAppearance(xObject.getPdfObject());
            }

            acroForm.flattenFields();

            pdfDocument.close();
        }
    }
}
