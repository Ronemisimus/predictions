package gui.history.display;

import javafx.application.Platform;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;

import java.util.Map;
import java.util.concurrent.*;

public class EntityChartLabel extends Label implements ChartAble{
    private final XYChart.Series<String, Number> series;

    private final ImageView chartImageView;

    /**
     * Creates an empty label
     */
    public EntityChartLabel(String entity, Map<String,Map<Integer,Integer>> counts, VBox parent) {
        super(entity + "Chart");
        series = new XYChart.Series<>();
        counts.get(entity).forEach((k,v) -> series.getData().add(new XYChart.Data<>(k.toString(),v)));
        series.setName("Entity Chart");
        BarChart<String, Number> barChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        barChart.getData().add(series);
        barChart.setTitle(entity);
        barChart.getXAxis().setLabel("tick");
        barChart.getYAxis().setLabel("amount");
        barChart.setAnimated(false);
        chartImageView = new ImageView();
        Object lock = new Object();
        new Thread(() -> sideLoadRender(barChart, parent, lock)).start();
    }

    private void sideLoadRender(BarChart<String, Number> barChart,
                                         VBox parent,
                                         Object lock) {
        Future<WritableImage> res = ChartRenderer.renderToImage(barChart, parent, lock);
        while(!res.isDone()) {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (lock) {
                try {
                    lock.wait(50);
                } catch (InterruptedException ignored) {
                }
            }
        }

        Platform.runLater(() -> {
            synchronized (this) {
                try {
                    chartImageView.setImage(res.get());
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


    @Override
    public synchronized void chart(VBox barChart) {
        if (chartImageView.getImage()!=null) {
            // Calculate the maxWidth and maxHeight based on the dimensions of the VBox
            double maxWidth = barChart.getWidth();
            double maxHeight = barChart.getHeight();

            // Calculate the scaling factors to fit the image within the calculated maxWidth and maxHeight
            double imageWidth = chartImageView.getImage().getWidth();
            double imageHeight = chartImageView.getImage().getHeight();

            double scale = Math.min(maxWidth / imageWidth, maxHeight / imageHeight);

            // Apply the scaling factors to the ImageView
            chartImageView.setFitWidth(imageWidth * scale);
            chartImageView.setFitHeight(imageHeight * scale);

            barChart.getChildren().clear();
            barChart.getChildren().add(chartImageView);
        }
    }
}
