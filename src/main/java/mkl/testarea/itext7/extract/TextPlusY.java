package mkl.testarea.itext7.extract;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="http://stackoverflow.com/questions/43746884/how-to-get-the-text-position-from-the-pdf-page-in-itext-7">
 * How to get the text position from the pdf page in iText 7
 * </a>
 * <p>
 * This class represents text plus y coordinates.
 * </p>
 * <p>
 * It is used to show how to extract text with its characters' respective y coordinates
 * from a document, cf. the test {@link ExtractTextPlusY}. 
 * </p>
 * <p>
 * Beware, this is but a proof-of-concept which in particular assumes text to be written
 * horizontally, i.e. using an effective transformation matrix with b and c equal to 0.
 * Furthermore the character and coordinate retrieval methods of this class are
 * not at all optimized and might take long to execute.
 * </p>
 * @author mkl
 */
public class TextPlusY implements CharSequence
{
    final List<String> texts = new ArrayList<>();
    final List<Float> yCoords = new ArrayList<>();

    //
    // CharSequence implementation
    //
    @Override
    public int length()
    {
        int length = 0;
        for (String text : texts)
        {
            length += text.length();
        }
        return length;
    }

    @Override
    public char charAt(int index)
    {
        for (String text : texts)
        {
            if (index < text.length())
            {
                return text.charAt(index);
            }
            index -= text.length();
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public CharSequence subSequence(int start, int end)
    {
        TextPlusY result = new TextPlusY();
        int length = end - start;
        for (int i = 0; i < yCoords.size(); i++)
        {
            String text = texts.get(i);
            if (start < text.length())
            {
                float yCoord = yCoords.get(i); 
                if (start > 0)
                {
                    text = text.substring(start);
                    start = 0;
                }
                if (length > text.length())
                {
                    result.add(text, yCoord);
                }
                else
                {
                    result.add(text.substring(0, length), yCoord);
                    break;
                }
            }
            else
            {
                start -= text.length();
            }
        }
        return result;
    }

    //
    // Object overrides
    //
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        for (String text : texts)
        {
            builder.append(text);
        }
        return builder.toString();
    }

    //
    // y coordinate support
    //
    public TextPlusY add(String text, float y)
    {
        if (text != null)
        {
            texts.add(text);
            yCoords.add(y);
        }
        return this;
    }

    public float yCoordAt(int index)
    {
        for (int i = 0; i < yCoords.size(); i++)
        {
            String text = texts.get(i);
            if (index < text.length())
            {
                return yCoords.get(i);
            }
            index -= text.length();
        }
        throw new IndexOutOfBoundsException();
    }
}
