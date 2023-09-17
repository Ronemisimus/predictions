package gui.history.display;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class EntityChartLabel extends Label implements ChartAble{
    private final XYChart.Series<String, Number> series;

    private final ImageView chartImageView;

    private final BarChart<String,Number> barChart;
    private final VBox parent;

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
        this.parent = parent;
        chartImageView = new ImageView();
    }


    @Override
    public synchronized void chart(VBox barChart) {
        if (chartImageView.getImage()==null){
            barChart.getChildren().clear();
            barChart.getChildren().add(this.barChart);
            new Thread(this::renderBarChart).start();
        }
        else {
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

    private void renderBarChart() {
        Future<WritableImage> res = ChartRenderer.renderToImage(this.barChart, this.parent);
        while(!res.isDone())
        {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            synchronized (this) {
                chartImageView.setImage(res.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
