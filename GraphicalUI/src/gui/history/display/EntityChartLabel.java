package gui.history.display;

import gui.EngineApi;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.util.Map;

public class EntityChartLabel extends Label implements ChartAble{
    private String entity;
    private XYChart.Series<String, Integer> series;

    /**
     * Creates an empty label
     */
    public EntityChartLabel(String entity, Map<String,Map<Integer,Integer>> counts) {
        super(entity + "Chart");
        this.entity = entity;
        series = new XYChart.Series<>();
        counts.get(entity).forEach((k,v) -> series.getData().add(new XYChart.Data<>(k.toString(),v)));
        series.setName("Entity Chart");
    }


    @Override
    public void chart(BarChart<String, Integer> barChart) {
        barChart.getData().add(series);
        barChart.setTitle(entity);
        barChart.getYAxis().setLabel("Entity Amount");
        barChart.getXAxis().setLabel("Ticks");
    }
}
