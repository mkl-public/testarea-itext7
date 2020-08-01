package mkl.testarea.itext7.extract;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class FieldValues<C> {
    public FieldValues(String name, C actualValue, Collection<? extends C> widgetValues) {
        this.name = name;
        this.actualValue = actualValue;
        this.widgetValues = new TreeSet<C>(widgetValues);
    }

    public C getActualValue() {
        return actualValue;
    }

    public Set<C> getWidgetValues() {
        return widgetValues;
    }

    final String name;
    final C actualValue;
    final Set<C> widgetValues;
}
