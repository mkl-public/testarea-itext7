package mkl.testarea.itext7.content;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.ColumnDocumentRenderer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;

/**
 * @author mkl
 */
public class MixedColumnLayout {
    final static File RESULT_FOLDER = new File("target/test-outputs", "content");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    /**
     * <a href="https://stackoverflow.com/questions/51615791/itext-7-mixed-full-body-and-column-layout-on-the-same-page">
     * Itext 7 mixed full body and column layout on the same page
     * </a>
     * <p>
     * To fix the issue of the updated code, two measures were necessary:
     * </p>
     * <ul>
     * <li>The {@link CustomParagraphRenderer} variable <b>y</b> needs to be static.
     * Currently the final renderer of the paragraph may actually not having been used
     * which leaves it with a 0 <b>y</b> value. If that variable is static, the most
     * recent actually determined value is used.
     * <li>After setting the renderer, before adding content, an {@link AreaBreak}
     * needs to be added to prevent the new renderer starting on the previous page.
     * This is a bit like weird magic. Hopefully someone can explain.
     * </ul>
     */
    @Test
    public void testMixedColumnsLikeJsb11111() throws IOException {
        File file = new File(RESULT_FOLDER, "mixedrenderersLikeJsb11111.pdf");

        try(
                PdfWriter pdfWriter = new PdfWriter(file);
                PdfDocument pdf = new PdfDocument(pdfWriter);
        ) {
            String [] loremipsums = {
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam interdum sollicitudin velit nec semper. Aliquam porta venenatis tortor, et viverra nisl accumsan non. Sed euismod tincidunt ex et porttitor. Duis lacinia efficitur auctor. Quisque eros quam, maximus et suscipit quis, tempor fringilla lorem. Donec hendrerit hendrerit vehicula. Integer vulputate fermentum arcu in tincidunt. Fusce euismod sapien id iaculis efficitur. Suspendisse potenti.",
                "Proin condimentum lorem a enim cursus tincidunt. Proin dui ex, faucibus semper tincidunt vitae, lobortis ut urna. Nullam iaculis neque accumsan urna consectetur accumsan. Mauris quis est nunc. Pellentesque vitae urna congue, dignissim lacus quis, volutpat ipsum. Duis arcu neque, convallis et nunc aliquet, sollicitudin finibus sem. Donec malesuada commodo purus. Quisque imperdiet elementum suscipit. Cras fringilla dolor a nunc placerat porta. In id consequat justo, eget dictum mauris. Sed felis est, tristique vulputate nunc non, bibendum consequat nibh. Ut imperdiet sit amet lectus sed bibendum. Sed vitae blandit nibh, at tincidunt nisi. Nunc vulputate mi in ipsum egestas posuere eget ac arcu. Duis at sagittis sapien.",
                "Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Aenean odio lorem, porttitor id ante id, dapibus blandit orci. Donec molestie luctus neque sit amet fermentum. Aliquam nec tempus nulla. Aenean nec auctor metus. Curabitur non ultrices enim. In nec orci efficitur, vestibulum sem ut, molestie metus. Aenean sit amet purus finibus, tempor nibh et, ultrices orci. Fusce elementum fringilla eros, vel facilisis justo placerat et. Proin sagittis, nunc vitae rutrum porttitor, libero risus vulputate ipsum, quis dignissim sapien orci non quam. Cras eu dolor volutpat, blandit lacus vitae, venenatis felis. Sed laoreet mi non turpis feugiat pharetra. Fusce sem est, condimentum at elit a, consequat condimentum mauris. Vestibulum est est, tincidunt sed varius ac, gravida eget purus. Pellentesque sit amet nibh sit amet mi tincidunt tempor non vitae quam.",
                "Sed a augue nunc. Suspendisse potenti. Praesent hendrerit sem lacus, sodales bibendum nunc pretium vel. Proin tincidunt, orci porttitor suscipit consectetur, neque dui fringilla neque, vitae dapibus orci libero quis leo. Nunc velit arcu, accumsan et felis ut, sodales varius libero. Quisque vitae iaculis ante. Suspendisse potenti. Vivamus fringilla sollicitudin mollis. Etiam nulla dolor, placerat at molestie a, lobortis et diam. Phasellus egestas aliquet pellentesque. Etiam pretium sapien sed nunc vehicula, in fermentum quam euismod. Proin auctor leo eu urna tempus, quis auctor felis bibendum. Quisque sollicitudin lacinia urna a ultricies. Ut volutpat eros tristique tempor imperdiet. Sed sit amet nulla non elit sollicitudin rutrum. In suscipit mollis purus, non efficitur justo molestie tempus.",
                "Quisque sed est odio. Ut et sodales nulla, ornare mollis nunc. Curabitur nec bibendum nunc. Phasellus lobortis auctor faucibus. Praesent quis metus at diam mollis laoreet. Nulla viverra risus in blandit interdum. Praesent sed tortor id felis tincidunt luctus nec vel dolor. Vivamus hendrerit, enim vel sollicitudin consequat, dui augue tincidunt metus, quis pellentesque dui ante non leo. Mauris ultricies elit id tempus vehicula. Nunc mauris arcu, accumsan quis lorem quis, pharetra tincidunt sem. Donec ut lacus molestie dolor convallis elementum tincidunt vitae sem. Fusce viverra tortor libero, vitae ultricies lectus hendrerit interdum. In hac habitasse platea dictumst. Fusce ante eros, pretium at pellentesque id, auctor et mi. Nam ut accumsan dolor, ac cursus elit. Nunc nec sapien blandit, volutpat tortor eget, aliquet lacus.",
            };
            Paragraph para = null;
            CustomParagraphRenderer paragraphRenderer = null;

            Document document = new Document(pdf, PageSize.A4);
            document.setRenderer(new DocumentRenderer(document));

            // One full body paragraph that does not flow onto subsequent pages ...
            para = new Paragraph().add(loremipsums[0]);
            paragraphRenderer = new CustomParagraphRenderer(para);
            para.setNextRenderer(paragraphRenderer);
            document.add(para);

            // ... followed by column layout starting on the same page and flowing onto subsequent pages.
            float y = paragraphRenderer.getY();
            float offSet = 36; // margins
            float gutter = 23; // column gap
            float columnWidth = (PageSize.A4.getWidth() - offSet * 2) / 2 - gutter;
            float columnHeight1 = y - offSet * 2;
            Rectangle[] columns1 = {
                new Rectangle(offSet, offSet, columnWidth, columnHeight1),
                new Rectangle(offSet + columnWidth + gutter, offSet, columnWidth, columnHeight1)
            };
            float columnHeight2 = PageSize.A4.getHeight() - offSet * 2;
            Rectangle[] columns2 = {
                new Rectangle(offSet, offSet, columnWidth, columnHeight2),
                new Rectangle(offSet + columnWidth + gutter, offSet, columnWidth, columnHeight2)};
            document.setRenderer(new CustomColumnDocumentRenderer(document, columns1, columns2));  
            for(int i = 0; i < 5; i++) {
                for(String loremipsum : loremipsums) {
                    document.add(new Paragraph(loremipsum));
                }
            }

            // Then followed by full body paragraphs stating on the page after the column layout and flowing onto subsequent pages ...
            document.setRenderer(new DocumentRenderer(document));
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            for(int i = 0; i < 5; i++) {
                for(String loremipsum : loremipsums) {
                    para = new Paragraph().add(loremipsum);
                    paragraphRenderer = new CustomParagraphRenderer(para);
                    para.setNextRenderer(paragraphRenderer);
                    document.add(para);
                }
            }


            // ... finally followed by column layout starting on the same page as the last paragraph as the previous full body layout, and flowing onto subsequent pages.
            // BUG: However - this starts rendering at the top of a new page. It does NOT begin rendering on the same page as the last full body paragraph previously rendered.
            // BUG: And some of the text in gets lost at the start of rendering this section.
            y = ((CustomParagraphRenderer)para.getRenderer()).getY();
            offSet = 36; // margins
            gutter = 23; // column gap
            columnWidth = (PageSize.A4.getWidth() - offSet * 2) / 2 - gutter;
            columnHeight1 = y - offSet * 2;
            columns1 = new Rectangle[2];
            columns1[0] = new Rectangle(offSet, offSet, columnWidth, columnHeight1);
            columns1[1] = new Rectangle(offSet + columnWidth + gutter, offSet, columnWidth, columnHeight1);
            columnHeight2 = PageSize.A4.getHeight() - offSet * 2;
            columns2 = new Rectangle[2];
            columns2[0] = new Rectangle(offSet, offSet, columnWidth, columnHeight2);
            columns2[1] = new Rectangle(offSet + columnWidth + gutter, offSet, columnWidth, columnHeight2);
            document.setRenderer(new CustomColumnDocumentRenderer(document, columns1, columns2));
            // v--- added AreaBreak
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            // ^--- added AreaBreak
            for(int i = 0; i < 5; i++) {
                for(String loremipsum : loremipsums) {
                    document.add(new Paragraph(loremipsum));
                }
            }

            document.flush();
            document.close();
        }
    }

    /**
     * @see MixedColumnLayout#testMixedColumnsLikeJsb11111()
     */
    public static class CustomColumnDocumentRenderer extends ColumnDocumentRenderer {

        private Rectangle [] columns2 = null;

        public CustomColumnDocumentRenderer(Document document, Rectangle [] columns1, Rectangle [] columns2) {
            super(document, columns1);
            this.columns2 = columns2;
        }

        @Override
        protected PageSize addNewPage(PageSize customPageSize) {
            PageSize size = super.addNewPage(customPageSize);
            super.columns = this.columns2;
            return size;
        }
    }

    /**
     * @see MixedColumnLayout#testMixedColumnsLikeJsb11111()
     */
    public static final class CustomParagraphRenderer extends ParagraphRenderer {
        // v--- variable made static
        private static float y = 0.0f;
        // ^--- variable made static

        public CustomParagraphRenderer(Paragraph modelElement) {
            super(modelElement);
        }

        @Override
        public void drawBorder(DrawContext drawContext) {
            super.drawBorder(drawContext);
            y = getOccupiedAreaBBox().getBottom();
        }

        @Override
        public IRenderer getNextRenderer() {
            return new CustomParagraphRenderer((Paragraph) modelElement);
        }

        public float getY() {
            return y;
        }
    }
}
