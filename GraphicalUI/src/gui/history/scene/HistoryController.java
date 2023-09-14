package gui.history.scene;

import gui.EngineApi;
import gui.history.display.RunDisplayed;
import javafx.fxml.FXML;

import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import java.util.List;
import java.util.Objects;
import java.util.Observable;

public class HistoryController {
    @FXML
    private ListView<RunDisplayed> HistoryList;
    @FXML
    private ListView CurrentRuns;
    @FXML
    private ListView<Parent> EndedRuns;
    @FXML
    private void initialize() {
        List<RunDisplayed> history = EngineApi.getInstance().getRunHistory();
        HistoryList.getItems().addAll(history);
        HistoryList.getSelectionModel().selectedItemProperty().addListener((Observable, oldVal, newVal) ->{
            if(newVal!=null)
            {
                EndedRuns.getItems().add(newVal.getDetailsView());
            }
        });
    }
}
