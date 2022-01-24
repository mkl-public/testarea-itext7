package mkl.testarea.itext7.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.pdf.PdfLiteral;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;

/**
 * @author mkl
 */
public class LimitedTextRotater extends PdfCanvasEditor {
    public LimitedTextRotater(Matrix rotation, Predicate<String> textMatcher) {
        super(new TextRetrievingListener());
        ((TextRetrievingListener)getEventListener()).limitedTextRotater = this;
        this.rotation = rotation;
        this.textMatcher = textMatcher;
    }

    @Override
    protected void write(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
        String operatorString = operator.toString();

        if (TEXT_SHOWING_OPERATORS.contains(operatorString)) {
            recentTextOperations.add(new ArrayList<>(operands));
        } else {
            if (!recentTextOperations.isEmpty()) {
                boolean rotate = textMatcher.test(text.toString());
                if (rotate)
                    writeSetTextMatrix(processor, rotation.multiply(initialTextMatrix));
                for (List<PdfObject> recentOperation : recentTextOperations) {
                    super.write(processor, (PdfLiteral) recentOperation.get(recentOperation.size() - 1), recentOperation);
                }
                if (rotate)
                    writeSetTextMatrix(processor, finalTextMatrix);
                recentTextOperations.clear();
                text.setLength(0);
                initialTextMatrix = null;
            }
            super.write(processor, operator, operands);
        }
    }

    void writeSetTextMatrix(PdfCanvasProcessor processor, Matrix textMatrix) {
        PdfLiteral operator = new PdfLiteral("Tm\n");
        List<PdfObject> operands = new ArrayList<>();
        operands.add(new PdfNumber(textMatrix.get(Matrix.I11)));
        operands.add(new PdfNumber(textMatrix.get(Matrix.I12)));
        operands.add(new PdfNumber(textMatrix.get(Matrix.I21)));
        operands.add(new PdfNumber(textMatrix.get(Matrix.I22)));
        operands.add(new PdfNumber(textMatrix.get(Matrix.I31)));
        operands.add(new PdfNumber(textMatrix.get(Matrix.I32)));
        operands.add(operator);
        super.write(processor, operator, operands);
    }

    void eventOccurred(TextRenderInfo textRenderInfo) {
        Matrix textMatrix = textRenderInfo.getTextMatrix();
        if (initialTextMatrix == null)
            initialTextMatrix = textMatrix;
        finalTextMatrix = new Matrix(textRenderInfo.getUnscaledWidth(), 0).multiply(textMatrix);

        text.append(textRenderInfo.getText());
    }

    static class TextRetrievingListener implements IEventListener {
        @Override
        public void eventOccurred(IEventData data, EventType type) {
            if (data instanceof TextRenderInfo) {
                limitedTextRotater.eventOccurred((TextRenderInfo) data);
            }
        }

        @Override
        public Set<EventType> getSupportedEvents() {
            return null;
        }

        LimitedTextRotater limitedTextRotater;
    }

    final static List<String> TEXT_SHOWING_OPERATORS = Arrays.asList("Tj", "'", "\"", "TJ");

    final Matrix rotation;
    final Predicate<String> textMatcher;

    final List<List<PdfObject>> recentTextOperations = new ArrayList<>();
    final StringBuilder text = new StringBuilder();
    Matrix initialTextMatrix = null;
    Matrix finalTextMatrix = null;
}
