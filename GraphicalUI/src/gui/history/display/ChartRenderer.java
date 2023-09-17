package gui.history.display;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.layout.AnchorPane;

import javax.swing.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;


public class ChartRenderer {
    public static Future<WritableImage> renderToImage(BarChart<String, Number> chart, VBox parent, Object futureLock) {

        WritableImage image = new WritableImage((int) parent.getWidth(), (int) parent.getHeight());
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(javafx.scene.paint.Color.TRANSPARENT); // Set a transparent background
        CompletableFuture<WritableImage> res = new CompletableFuture<>();
        final JFrame[] stage = {null};
        final AnchorPane chartLayout = new AnchorPane();
        Object lock = new Object();
        Platform.runLater(() -> {
            stage[0] = new JFrame();
            layoutChart(chart, parent, chartLayout, lock, stage[0]);
        });
        while (chartLayout.getChildren().isEmpty())
        {
            synchronized (lock) {
                try {
                    lock.wait(50);
                } catch (InterruptedException ignored) {
                }
            }
        }
        Platform.runLater(() -> {
            try {
                synchronized (futureLock) {
                    WritableImage snapshot = chartLayout.snapshot(parameters, image);
                    res.complete(snapshot);
                    futureLock.notifyAll();
                }
            } catch (Exception e) {
                res.completeExceptionally(e); // Handle exceptions
            }
            finally {
                stage[0].dispose();
            }
        });
        return res;
    }

    private static void layoutChart(BarChart<String, Number> chart, VBox parent, AnchorPane anchorPane, Object lock, JFrame stage) {
        int width = (int) parent.getWidth();
        int height = (int) parent.getHeight();
        anchorPane.setMinSize(width, height);
        anchorPane.setMaxSize(width, height);
        anchorPane.setPrefSize(width, height);
        final ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(anchorPane);
        anchorPane.getChildren().clear();
        AnchorPane.setLeftAnchor(chart, 0.0);
        AnchorPane.setRightAnchor(chart, 0.0);
        AnchorPane.setTopAnchor(chart, 0.0);
        AnchorPane.setBottomAnchor(chart, 0.0);
        final JFXPanel jfxPanel = new JFXPanel();
        jfxPanel.setScene(new Scene(scrollPane, width, height));
        stage.add(jfxPanel);
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (lock) {
            anchorPane.getChildren().add(chart);
            anchorPane.layout();
            lock.notifyAll();
        }
    }
}
