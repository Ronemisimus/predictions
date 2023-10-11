package clientGui.history.display;

import clientGui.history.data.PropertyData;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.stream.Collectors;

public class PropertyChartLabel extends Label implements ChartAble {
    private final HBox legend;

    private final ScrollPane graph;

    private final String name;

    public PropertyChartLabel(String entity, String property, PropertyData propertyData) {
        super(entity + "." + property);
        legend = new HBox();
        legend.getChildren().add(new Label("consistency: " + propertyData.getConsistency()));
        legend.getChildren().add(new Label("average: " + propertyData.getAverage()));
        legend.setSpacing(10);
        legend.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));
        this.name = entity + "." + property;
        this.graph = new ScrollPane();
        graph.setFitToWidth(true);
        graph.setFitToHeight(true);
        final TableView<HistogramLine> table = new TableView<>();
        TableColumn<HistogramLine,Comparable<?>> valueColumn = new TableColumn<>("Value");
        TableColumn<HistogramLine,Integer> countColumn = new TableColumn<>("Count");
        valueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
        countColumn.setCellValueFactory(cellData -> cellData.getValue().countProperty());
        //noinspection unchecked
        table.getColumns().addAll(valueColumn, countColumn);

        table.setItems(FXCollections.observableArrayList(
                propertyData.getHistogram().entrySet().stream()
                        .map(entry -> new HistogramLine(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toList())
        ));

        graph.setContent(table);
    }

    @Override
    public void chart(VBox barChart) {
        barChart.getChildren().clear();
        barChart.getChildren().addAll(graph, legend);
    }

    @Override
    public String toString() {
        return "PropertyChartLabel{" +
                "name='" + name + '\'' +
                '}';
    }
}
