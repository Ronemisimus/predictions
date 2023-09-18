package gui.history.display;

import dto.subdto.show.world.EntityDto;
import gui.EngineApi;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class EntityCountDisplay {
    private final Integer identifier;
    private final ObservableList<HistogramLine> entityChartData = FXCollections.observableArrayList();

    private final ScheduledExecutorService amountGetter;

    private Future<Void> task = null;

    private final TableView<HistogramLine> table;

    public EntityCountDisplay(Integer identifier) {
        this.identifier = identifier;
        amountGetter = Executors.newScheduledThreadPool(1);
        TableColumn<HistogramLine, Comparable<?>> nameColumn = new TableColumn<>("Value");
        TableColumn<HistogramLine, Integer> amountColumn = new TableColumn<>("Amount");
        nameColumn.setCellValueFactory(histLine -> histLine.getValue().valueProperty());
        amountColumn.setCellValueFactory(histLine -> histLine.getValue().countProperty());
        table = new TableView<>();
        nameColumn.prefWidthProperty().bind(table.widthProperty().divide(2));
        amountColumn.prefWidthProperty().bind(table.widthProperty().divide(2));
        //noinspection unchecked
        table.getColumns().addAll(nameColumn, amountColumn);
        table.setItems(entityChartData);
    }

    public void Display(Pane node) {
        //noinspection unchecked
        task = (Future<Void>) amountGetter.scheduleAtFixedRate(this::getAmounts, 0, 500, TimeUnit.MILLISECONDS);
        Platform.runLater(() -> {
            node.getChildren().clear();
            node.getChildren().add(table);
            table.prefWidthProperty().bind(node.widthProperty());
            table.prefHeightProperty().bind(node.heightProperty());
        });
    }

    private void getAmounts() {
        List<EntityDto> entities = EngineApi.getInstance().getCurrentEntityAmounts(identifier);
        List<HistogramLine> data = entities.stream()
                .map(entity -> new HistogramLine(
                        entity.getName(),
                        entity.getAmount()))
                .collect(Collectors.toList());

        List<HistogramLine> changedData = entityChartData.stream()
                .filter(line -> !data.contains(line))
                .collect(Collectors.toList());
        List<HistogramLine> newData = data.stream()
                .filter(line -> !entityChartData.contains(line))
                .collect(Collectors.toList());
        Platform.runLater(() -> {
            entityChartData.removeAll(changedData);
            entityChartData.addAll(newData);
        });
    }

    public void hide() {
        if (task!=null) {
            task.cancel(true);
            task = null;
        }
    }
}
