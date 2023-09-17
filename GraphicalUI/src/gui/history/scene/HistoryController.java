package gui.history.scene;

import gui.EngineApi;
import gui.MainController;
import gui.history.data.PropertyData;
import gui.history.data.RunState;
import gui.history.display.ChartAble;
import gui.history.display.EntityChartLabel;
import gui.history.display.PropertyChartLabel;
import gui.history.display.RunDisplayed;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HistoryController {
    @FXML
    private ListView<RunDisplayed> HistoryList;
    @FXML
    @SuppressWarnings("unused")
    private VBox CurrentRuns; // TODO: fill with running simulation data
    @FXML
    private ListView<Parent> EndedRuns;
    @FXML
    private VBox chart;

    private final ObservableList<RunDisplayed> runs = FXCollections.observableArrayList();

    private final ScheduledExecutorService historyGetter = Executors.newScheduledThreadPool(1);
    @FXML
    private void initialize() {
        HistoryList.getSelectionModel().selectedItemProperty().addListener(this::handleRunSelection);

        EndedRuns.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue instanceof ChartAble) {
                Platform.runLater(() -> {
                    System.out.println("selected: " + newValue);
                    chart.getChildren().clear();
                    chart.getChildren().add(new Label("loading..."));
                });
                Platform.runLater(() -> ((ChartAble) newValue).chart(chart));
            }
        });

        HistoryList.setItems(runs);

        historyGetter.scheduleAtFixedRate(() -> {
            List<RunDisplayed> history = EngineApi.getInstance().getRunHistory();

            List<RunDisplayed> replacedItems = runs.stream().filter(e -> !history.contains(e)).collect(Collectors.toList());
            List<RunDisplayed> addedItems = history.stream().filter(e -> !runs.contains(e)).collect(Collectors.toList());

            Platform.runLater(() -> updateUI(replacedItems,addedItems));
        },0, 1000, TimeUnit.MILLISECONDS);

        MainController.getInstance(null).getCenterStage().centerProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && newValue == null)
            {
                historyGetter.shutdownNow();
            }
        });
    }

    private void updateUI(List<RunDisplayed> replacedItems, List<RunDisplayed> addedItems) {
        runs.removeAll(replacedItems);
        runs.addAll(addedItems);
    }

    private void handleRunSelection(ObservableValue<? extends RunDisplayed> Observable, RunDisplayed oldVal, RunDisplayed newVal) {
        if (newVal != null && Stream.of(RunState.FINISHED, RunState.STOPPED).anyMatch(e -> e.equals(newVal.getRunState()))) {

            new Thread(() -> endedRunGetter(newVal)).start();
        }
    }

    private void endedRunGetter(RunDisplayed run) {

        Platform.runLater(() -> {
            EndedRuns.getItems().clear();
            EndedRuns.getItems().add(new Label("loading..."));
        });

        Map<String, Map<Integer, Integer>> counts = EngineApi.getInstance()
                .getSingleRunHistoryEntityAmount(run.getRunIdentifier());
        List<EntityChartLabel> entityLabels = counts.keySet().stream()
                .map(entity -> new EntityChartLabel(entity, counts, chart))
                .collect(Collectors.toList());

        List<Parent>  entitiesAdded = entityLabels.stream()
                .filter(e -> !run.getEntityChartLabels().contains(e))
                .collect(Collectors.toList());
        List<Parent> entitiesRemoved = run.getEntityChartLabels().stream()
                .filter(e -> !entityLabels.contains(e))
                .collect(Collectors.toList());

        Map<String, Map<String, PropertyData>> histograms = EngineApi.getInstance()
                .getSingleRunHistoryPropertyData(run.getRunIdentifier());
        List<PropertyChartLabel> propertyLabels = histograms.keySet().stream()
                .map(entity ->
                        histograms.get(entity).keySet()
                                .stream().map(property ->
                                        new PropertyChartLabel(entity, property, histograms.get(entity).get(property))
                                ).collect(Collectors.toList()))
                .flatMap(List::stream)
                .collect(Collectors.toList());

        List<Parent> propertiesAdded = propertyLabels.stream()
                .filter(e -> !run.getEntityChartLabels().contains(e))
                .collect(Collectors.toList());
        List<Parent> propertiesRemoved = run.getEntityChartLabels().stream()
                .filter(e -> !propertyLabels.contains(e))
                .collect(Collectors.toList());

        run.removeEntityChartLabels(entitiesRemoved);
        run.addEntityChartLabels(entitiesAdded);
        run.addEntityChartLabels(propertiesAdded);
        run.removeEntityChartLabels(propertiesRemoved);

        Platform.runLater(() -> EndedRuns.setItems(run.getEntityChartLabels()));
    }

}

