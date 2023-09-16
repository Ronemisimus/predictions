package gui.history.display;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.Map;

public class EntityChartLabel extends Label implements ChartAble{
    private final XYChart.Series<String, Number> series;

    private final BarChart<String, Number> barChart;

    /**
     * Creates an empty label
     */
    public EntityChartLabel(String entity, Map<String,Map<Integer,Integer>> counts) {
        super(entity + "Chart");
        series = new XYChart.Series<>();
        counts.get(entity).forEach((k,v) -> series.getData().add(new XYChart.Data<>(k.toString(),v)));
        series.setName("Entity Chart");
        barChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        barChart.getData().add(series);
        barChart.setTitle(entity);
        barChart.getXAxis().setLabel("tick");
        barChart.getYAxis().setLabel("amount");
    }


    @Override
    public void chart(VBox barChart) {
        barChart.getChildren().add(this.barChart);
    }
}
