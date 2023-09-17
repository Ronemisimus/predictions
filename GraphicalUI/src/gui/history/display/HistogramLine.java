package gui.history.display;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class HistogramLine {
    private final Property<Comparable<?>> value;
    private final Property<Integer> count;

    public HistogramLine(Comparable<?> value, Integer count) {
        this.value = new SimpleObjectProperty<>(value);
        this.count = new SimpleIntegerProperty(count).asObject();
    }

    public Property<Comparable<?>> valueProperty() {
        return value;
    }

    public Property<Integer> countProperty() {
        return count;
    }

}
