package clientGui.history.display;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.Map;

public class EntityChartLabel extends Label implements ChartAble{
    private final ImageView chartImageView;

    public EntityChartLabel(String entity, Map<String,Map<Integer,Integer>> counts, VBox parent) {
        super(entity + "Chart");
        chartImageView = new ImageView();
        ChartImageRenderer chartImageRenderer = new ChartImageRenderer(
                (int) parent.getWidth(),
                (int) parent.getHeight(),
                entity + " Chart",
                "ticks",
                "Amount");
        chartImageRenderer.buildXYChart(counts.get(entity));
        Image graph = SwingFXUtils.toFXImage(chartImageRenderer.createGraphImage(), null);
        Platform.runLater(() -> chartImageView.setImage(graph));
    }

    @Override
    public synchronized void chart(VBox barChart) {
        chartImageView.fitWidthProperty().bind(barChart.widthProperty());
        chartImageView.fitHeightProperty().bind(barChart.heightProperty());
        barChart.getChildren().clear();
        barChart.getChildren().add(chartImageView);
    }
}
