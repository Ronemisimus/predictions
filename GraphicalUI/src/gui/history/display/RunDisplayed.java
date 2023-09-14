package gui.history.display;

import gui.EngineApi;
import gui.details.tree.OpenableItem;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Box;
import javafx.scene.text.Text;
import predictions.definition.entity.EntityDefinition;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

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
