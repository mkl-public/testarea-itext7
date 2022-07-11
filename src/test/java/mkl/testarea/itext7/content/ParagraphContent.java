package mkl.testarea.itext7.content;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.itextpdf.layout.element.IAbstractElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;

/**
 * @author mkl
 */
public class ParagraphContent {

    /**
     * <a href="https://stackoverflow.com/questions/72929075/how-to-view-the-text-of-itext7-com-itextpdf-layout-element-paragraph-in-java">
     * How to view the text of Itext7 com.itextpdf.layout.element.Paragraph in Java
     * </a>
     * <p>
     * This test shows how to extract the content from the {@link Paragraph} element
     * given as example by the OP.
     * </p>
     * @see #testSubParagraph()
     * @see #testTable()
     */
    @Test
    public void testKishoresExample() {
        Paragraph p = new Paragraph(new Text("Content1")).add(" Content2");
        assertEquals("Content1 Content2", getContent(p));
    }

    /**
     * <a href="https://stackoverflow.com/questions/72929075/how-to-view-the-text-of-itext7-com-itextpdf-layout-element-paragraph-in-java">
     * How to view the text of Itext7 com.itextpdf.layout.element.Paragraph in Java
     * </a>
     * <p>
     * This test shows how to extract the content from a {@link Paragraph} element
     * with a sub-paragraph.
     * </p>
     * @see #testKishoresExample()
     * @see #testTable()
     */
    @Test
    public void testSubParagraph() {
        Paragraph p = new Paragraph(new Text("Content1")).add(new Paragraph(" Content2"));
        assertEquals("Content1 Content2", getContent(p));
    }

    /**
     * <a href="https://stackoverflow.com/questions/72929075/how-to-view-the-text-of-itext7-com-itextpdf-layout-element-paragraph-in-java">
     * How to view the text of Itext7 com.itextpdf.layout.element.Paragraph in Java
     * </a>
     * <p>
     * This test shows how to extract the content from a {@link Table} element.
     * </p>
     * @see #testKishoresExample()
     * @see #testSubParagraph()
     */
    @Test
    public void testTable() {
        Table t = new Table(3);
        t.addCell("one");
        t.addCell("two");
        t.addCell("three");

        assertEquals("onetwothree", getContent(t));
    }

    /**
     * Extracts the content from the {@link Text} elements in the given {@link Element}.
     * 
     * @see #testKishoresExample()
     * @see #testSubParagraph()
     * @see #testTable()
     */
    String getContent(IElement element) {
        StringBuilder builder = new StringBuilder();
        if (element instanceof Text) {
            builder.append(((Text)element).getText());
        }
        if (element instanceof IAbstractElement) {
            for (IElement child : ((IAbstractElement)element).getChildren()) {
                builder.append(getContent(child));
            }
        }
        return builder.toString();
    }
}
