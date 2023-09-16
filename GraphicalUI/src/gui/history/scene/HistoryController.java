package gui.history.scene;

import gui.EngineApi;
import gui.history.data.PropertyData;
import gui.history.display.ChartAble;
import gui.history.display.EntityChartLabel;
import gui.history.display.PropertyChartLabel;
import gui.history.display.RunDisplayed;
import javafx.fxml.FXML;

import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;

public class HistoryController {
    @FXML
    private ListView<RunDisplayed> HistoryList;
    @FXML
    @SuppressWarnings("unused")
    private VBox CurrentRuns; // TODO: fill with running simulation data
    @FXML
    private ListView<Parent> EndedRuns;
    @FXML
    private VBox chart;
    @FXML
    private void initialize() {
        List<RunDisplayed> history = EngineApi.getInstance().getRunHistory();
        HistoryList.getItems().addAll(history);
        HistoryList.getSelectionModel().selectedItemProperty().addListener((Observable, oldVal, newVal) ->{
            if(newVal!=null)
            {
                EndedRuns.getItems().removeAll();

                Map<String, Map<Integer,Integer>> counts = EngineApi.getInstance().getSingleRunHistoryEntityAmount(newVal.getRunIdentifier());

                EndedRuns.getItems().clear();
                for (String entity : counts.keySet())
                {
                    EndedRuns.getItems().add(new EntityChartLabel(entity, counts));
                }

                Map<String, Map<String, PropertyData>> histograms = EngineApi.getInstance().getSingleRunHistoryPropertyData(newVal.getRunIdentifier());

                for (String entity : histograms.keySet())
                {
                    for (String property : histograms.get(entity).keySet())
                    {
                        EndedRuns.getItems().add(new PropertyChartLabel(entity, property, histograms.get(entity).get(property)));
                    }
                }

                EndedRuns.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue instanceof ChartAble)
                    {
                        chart.getChildren().clear();
                        ((ChartAble) newValue).chart(chart);
                    }
                });
            }
        });
    }
}
