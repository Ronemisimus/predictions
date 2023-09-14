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

public class RunDisplayed extends HBox implements OpenableItem {

    private final Map.Entry<Integer, LocalDateTime> entry;
    public RunDisplayed(Map.Entry<Integer, LocalDateTime> entry) {
        super();
        this.entry = entry;
        Text text = new Text(entry.getKey() + " " + entry.getValue());
        this.getChildren().add(text);

    }

    @Override
    public Parent getDetailsView() {
        Map<String, Map.Entry<Integer,Integer>> res = EngineApi.getInstance().getSingleRunHistoryEntityAmount(entry.getKey());
        VBox vbox = new VBox();
        vbox.getChildren().addAll(
                res.entrySet().stream()
                        .map(e -> new Label(e.getKey() + ": " + e.getValue().getKey() + "/" + e.getValue().getValue()))
                        .collect(Collectors.toList())
        );
        return vbox;
    }
}
