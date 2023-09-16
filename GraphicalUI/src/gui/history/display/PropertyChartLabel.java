package gui.history.display;

import gui.history.data.PropertyData;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PropertyChartLabel extends Label implements ChartAble {

    private final XYChart.Series<String, Number> series;

    private final HBox legend;

    private final BarChart<String, Number> histogram;

    public PropertyChartLabel(String entity, String property, PropertyData propertyData) {
        super(entity + "." + property);
        series = new XYChart.Series<>();
        series.setName(entity + "." + property);
        propertyData.getHistogram().forEach((k, v) -> series.getData().add(new XYChart.Data<>(k.toString(), v)));
        legend = new HBox();
        legend.getChildren().add(new Label("consistency: " + propertyData.getConsistency()));
        legend.getChildren().add(new Label("average: " + propertyData.getAverage()));
        legend.setSpacing(10);
        legend.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));
        histogram = new BarChart<>(new CategoryAxis(), new NumberAxis());
        histogram.getData().add(series);
        histogram.setTitle(entity + "." + property);
        histogram.getXAxis().setLabel("value");
        histogram.getYAxis().setLabel("count");
    }

    @Override
    public void chart(VBox barChart) {
        barChart.getChildren().addAll(histogram, legend);
    }
}
