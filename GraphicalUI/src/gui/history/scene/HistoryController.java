package gui.history.scene;

import dto.subdto.show.world.EntityDto;
import gui.EngineApi;
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
                    Label entityChart = new Label(entity + " Chart");
                    EndedRuns.getItems().add(entityChart);
                }

                EndedRuns.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue!=null && newValue instanceof Label)
                    {
                        String entity = ((Label) newValue).getText();
                        entity = entity.substring(0,entity.indexOf(" Chart"));
                        Map<String, Map<Integer,Integer>> updated_counts = EngineApi.getInstance().getSingleRunHistoryEntityAmount(newVal.getRunIdentifier());
                        XYChart.Series<String, Integer> series = new XYChart.Series<>();
                        updated_counts.get(entity).forEach((k,v) -> series.getData().add(new XYChart.Data<>(k.toString(),v)));
                        series.setName("Entity Chart");
                        chart.getData().clear();
                        chart.getData().add(series);

                        chart.getYAxis().setLabel("Entity Amount");
                        chart.getXAxis().setLabel("Ticks");
                    }
                });

                // TODO: add labels for every entity.property pair
            }
        });
    }
}
