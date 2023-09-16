package gui.history.scene;

import dto.subdto.show.world.EntityDto;
import gui.EngineApi;
import gui.history.display.ChartAble;
import gui.history.display.EntityChartLabel;
import gui.history.display.RunDisplayed;
import javafx.fxml.FXML;

import javafx.scene.Parent;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import java.util.List;
import java.util.Map;

public class HistoryController {
    @FXML
    private ListView<RunDisplayed> HistoryList;
    @FXML
    private ListView CurrentRuns;
    @FXML
    private ListView<Parent> EndedRuns;
    @FXML
    private BarChart<String, Integer> chart;
    @FXML
    private void initialize() {
        chart.setAnimated(false);
        List<RunDisplayed> history = EngineApi.getInstance().getRunHistory();
        HistoryList.getItems().addAll(history);
        HistoryList.getSelectionModel().selectedItemProperty().addListener((Observable, oldVal, newVal) ->{
            if(newVal!=null)
            {
                EndedRuns.getItems().removeAll();

                chart.getData().clear();

                Map<String, Map<Integer,Integer>> counts = EngineApi.getInstance().getSingleRunHistoryEntityAmount(newVal.getRunIdentifier());

                EndedRuns.getItems().clear();
                for (String entity : counts.keySet())
                {
                    EntityChartLabel entityChart = new EntityChartLabel(entity, counts);
                    EndedRuns.getItems().add(entityChart);
                }

                EndedRuns.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue instanceof ChartAble)
                    {
                        chart.getData().clear();
                        ((ChartAble) newValue).chart(chart);
                    }
                });

                // TODO: add labels for every entity.property pair
            }
        });
    }
}
