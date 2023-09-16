package gui.history.display;

import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.time.LocalDateTime;
import java.util.Map;

public class RunDisplayed extends HBox {

    private final Map.Entry<Integer, LocalDateTime> entry;
    public RunDisplayed(Map.Entry<Integer, LocalDateTime> entry) {
        super();
        this.entry = entry;
        Text text = new Text(entry.getKey() + " " + entry.getValue());
        this.getChildren().add(text);

    }

    public Integer getRunIdentifier() {
        return entry.getKey();
    }

    public LocalDateTime getRunTime() {
        return entry.getValue();
    }
}
