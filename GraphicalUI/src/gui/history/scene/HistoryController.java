package gui.history.scene;

import gui.EngineApi;
import gui.history.display.RunDisplayed;
import javafx.fxml.FXML;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import java.util.List;

public class HistoryController {
    @FXML
    private ListView HistoryList;
    @FXML
    private ListView CurrentRuns;
    @FXML
    private ListView EndedRuns;
    @FXML
    private void initialize() {
        List<RunDisplayed> history = EngineApi.getInstance().getRunHistory();
        HistoryList.getItems().forEach(item-> ((RunDisplayed)item).focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
            {

            }
        }));
        HistoryList.getItems().addAll(history);
    }
}
