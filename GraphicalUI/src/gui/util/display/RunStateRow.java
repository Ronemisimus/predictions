package gui.util.display;

import javafx.beans.property.*;

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
