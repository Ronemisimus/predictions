package clientGui.scene.details;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RunStateRow {
    private final StringProperty state;
    private final Property<Integer> count;

    public RunStateRow(String state, Integer count) {
        this.state = new SimpleStringProperty(state);
        this.count = new SimpleIntegerProperty(count).asObject();
    }

    public StringProperty stateProperty() {
        return state;
    }

    public Property<Integer> countProperty() {
        return count;
    }
}
