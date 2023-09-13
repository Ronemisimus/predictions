package gui.history.display;

import javafx.scene.layout.HBox;
import javafx.scene.shape.Box;
import javafx.scene.text.Text;

import java.time.LocalDateTime;
import java.util.Map;

public class RunDisplayed extends HBox {

    public RunDisplayed(Map.Entry<Integer, LocalDateTime> entry) {
        super();
        Text text = new Text(entry.getKey() + " " + entry.getValue());
        this.getChildren().add(text);

    }
}
