package gui.history.display;

import javafx.application.Platform;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class ChartRenderer {
    public static Future<WritableImage> renderToImage(BarChart<String, Number> chart, VBox parent) {
        WritableImage image = new WritableImage((int) parent.getWidth(), (int) parent.getHeight());
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(javafx.scene.paint.Color.TRANSPARENT); // Set a transparent background
        CompletableFuture<WritableImage> res = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                WritableImage snapshot = chart.snapshot(parameters, image);
                res.complete(snapshot);
            } catch (Exception e) {
                res.completeExceptionally(e); // Handle exceptions
            }
        });
        return res;
    }
}
