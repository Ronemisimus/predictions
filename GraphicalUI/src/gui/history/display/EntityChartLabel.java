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

    private final VBox parent;

    private final Object lock;

    private final BarChart<String, Number> barChart;

    /**
     * Creates an empty label
     */
    public EntityChartLabel(String entity, Map<String,Map<Integer,Integer>> counts, VBox parent) {
        super(entity + "Chart");
        series = new XYChart.Series<>();
        counts.get(entity).forEach((k,v) -> series.getData().add(new XYChart.Data<>(k.toString(),v)));
        series.setName("Entity Chart");
        barChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        barChart.getData().add(series);
        barChart.setTitle(entity);
        barChart.getXAxis().setLabel("tick");
        barChart.getYAxis().setLabel("amount");
        barChart.setAnimated(false);
        chartImageView = new ImageView();
        this.lock = new Object();
        this.parent = parent;

    }

    private void sideLoadRender(BarChart<String, Number> barChart,
                                         VBox parent,
                                         Object lock,
                                Object chartLock) {
        Future<WritableImage> res = ChartRenderer.renderToImage(barChart, parent, lock);
        while(!res.isDone()) {
            synchronized (lock) {
                try {
                    lock.wait(50);
                } catch (InterruptedException ignored) {
                }
            }
        }

        Platform.runLater(() -> {
            synchronized (chartLock) {
                try {
                        chartImageView.setImage(res.get());
                        chartLock.notifyAll();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


    @Override
    public void chart(VBox barChart) {
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
        else
        {
            Object chartLock = new Object();
            new Thread(() -> sideLoadRender(this.barChart, parent, lock, chartLock)).start();
            new Thread(() -> {
                synchronized (chartLock) {
                    while (chartImageView.getImage() == null) {
                        try {
                            chartLock.wait(1);
                        } catch (InterruptedException ignored) {
                        }
                    }
                    Platform.runLater(() -> this.chart(barChart));
                }
            }).start();
        }
    }
}
