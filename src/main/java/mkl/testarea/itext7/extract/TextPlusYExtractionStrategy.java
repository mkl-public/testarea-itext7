package mkl.testarea.itext7.extract;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;

/**
 * <a href="http://stackoverflow.com/questions/43746884/how-to-get-the-text-position-from-the-pdf-page-in-itext-7">
 * How to get the text position from the pdf page in iText 7
 * </a>
 * <p>
 * This text extraction strategy is derived from the {@link LocationTextExtractionStrategy}
 * and tweaked to also return a {@link TextPlusY} instance which contains text plus y coordinates. 
 * </p>
 * <p>
 * It is used to show how to extract text with its characters' respective y coordinates
 * from a document, cf. the test {@link ExtractTextPlusY}. 
 * </p>
 * <p>
 * Beware, this is but a proof-of-concept which in particular assumes text to be written
 * horizontally, i.e. using an effective transformation matrix with b and c equal to 0.
 * Furthermore the character and coordinate retrieval methods of {@link TextPlusY} are
 * not at all optimized and might take long to execute.
 * </p>
 * @author mkl
 */
public class TextPlusYExtractionStrategy extends LocationTextExtractionStrategy
{
    static Field locationalResultField;
    static Method sortWithMarksMethod;
    static Method startsWithSpaceMethod;
    static Method endsWithSpaceMethod;

    static Method textChunkSameLineMethod;

    static
    {
        try
        {
            locationalResultField = LocationTextExtractionStrategy.class.getDeclaredField("locationalResult");
            locationalResultField.setAccessible(true);
            sortWithMarksMethod = LocationTextExtractionStrategy.class.getDeclaredMethod("sortWithMarks", List.class);
            sortWithMarksMethod.setAccessible(true);
            startsWithSpaceMethod = LocationTextExtractionStrategy.class.getDeclaredMethod("startsWithSpace", String.class);
            startsWithSpaceMethod.setAccessible(true);
            endsWithSpaceMethod = LocationTextExtractionStrategy.class.getDeclaredMethod("endsWithSpace", String.class);
            endsWithSpaceMethod.setAccessible(true);

            textChunkSameLineMethod = TextChunk.class.getDeclaredMethod("sameLine", TextChunk.class);
            textChunkSameLineMethod.setAccessible(true);
        }
        catch(NoSuchFieldException | NoSuchMethodException | SecurityException e)
        {
            // Reflection failed
        }
    }

    //
    // constructors
    //
    public TextPlusYExtractionStrategy()
    {
        super();
    }

    public TextPlusYExtractionStrategy(ITextChunkLocationStrategy strat)
    {
        super(strat);
    }

    @Override
    public String getResultantText()
    {
        return getResultantTextPlusY().toString();
    }

    public TextPlusY getResultantTextPlusY()
    {
        try
        {
            List<TextChunk> textChunks = new ArrayList<>((List<TextChunk>)locationalResultField.get(this));
            sortWithMarksMethod.invoke(this, textChunks);

            TextPlusY textPlusY = new TextPlusY();
            TextChunk lastChunk = null;
            for (TextChunk chunk : textChunks)
            {
                float chunkY = chunk.getLocation().getStartLocation().get(Vector.I2);
                if (lastChunk == null)
                {
                    textPlusY.add(chunk.getText(), chunkY);
                }
                else if ((Boolean)textChunkSameLineMethod.invoke(chunk, lastChunk))
                {
                    // we only insert a blank space if the trailing character of the previous string wasn't a space, and the leading character of the current string isn't a space
                    if (isChunkAtWordBoundary(chunk, lastChunk) &&
                            !(Boolean)startsWithSpaceMethod.invoke(this, chunk.getText()) &&
                            !(Boolean)endsWithSpaceMethod.invoke(this, lastChunk.getText()))
                    {
                        textPlusY.add(" ", chunkY);
                    }

                    textPlusY.add(chunk.getText(), chunkY);
                }
                else
                {
                    textPlusY.add("\n", lastChunk.getLocation().getStartLocation().get(Vector.I2));
                    textPlusY.add(chunk.getText(), chunkY);
                }
                lastChunk = chunk;
            }

            return textPlusY;
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
        {
            throw new RuntimeException("Reflection failed", e);
        }
    }
}
