package clientGui.history.scene;

import clientGui.history.data.PropertyData;
import clientGui.history.data.RunState;
import clientGui.history.display.ChartAble;
import clientGui.history.display.EntityChartLabel;
import clientGui.history.display.PropertyChartLabel;
import clientGui.history.display.RunDisplayed;
import clientGui.scene.SceneController;
import clientGui.util.ServerApi;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HistoryController implements SceneController {
    @FXML
    private ListView<RunDisplayed> HistoryList;
    @FXML
    private ListView<Parent> EndedRuns;
    @FXML
    private VBox chart;
    @FXML
    private VBox currentRunEntityCount; // TODO: fill with running simulation data
    @FXML
    private ScrollPane CurrentRunProgress;

    private final ObservableList<RunDisplayed> runs = FXCollections.observableArrayList();

    private ScheduledExecutorService historyGetter;

    private static HistoryController instance = null;

    public static SceneController getInstance() {
        return instance;
    }

    @FXML
    private void initialize() {
        instance = this;
        HistoryList.getSelectionModel().selectedItemProperty().addListener(this::handleRunSelection);
        EndedRuns.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue instanceof ChartAble) {
                Platform.runLater(() -> {
                    chart.getChildren().clear();
                    chart.getChildren().add(new Label("loading..."));
                });
                Platform.runLater(() -> ((ChartAble) newValue).chart(chart));
            }
        });

        HistoryList.setItems(runs);

        historyGetter = Executors.newScheduledThreadPool(1);

        historyGetter.scheduleAtFixedRate(() -> {
            List<RunDisplayed> history = ServerApi.getInstance().getRunHistory();

            List<RunDisplayed> replacedItems = runs.stream().filter(e -> !history.contains(e)).collect(Collectors.toList());
            List<RunDisplayed> addedItems = history.stream().filter(e -> !runs.contains(e)).collect(Collectors.toList());

            Platform.runLater(() -> updateUI(replacedItems,addedItems));
        },0, 1000, TimeUnit.MILLISECONDS);
    }

    private void updateUI(List<RunDisplayed> replacedItems, List<RunDisplayed> addedItems) {
        runs.replaceAll(item -> {
            RunDisplayed replaced = replacedItems.stream().filter(e -> e.getRunTime().equals(item.getRunTime())).findFirst().orElse(null);
            if (replaced!=null) {
                replacedItems.remove(replaced);
                RunDisplayed added = addedItems.stream().filter(e -> e.getRunTime().equals(item.getRunTime())).findFirst().orElse(null);
                if (added!=null) {
                    addedItems.remove(added);
                    return added;
                }
            }
            return item;
        });
        runs.addAll(addedItems);
    }

    private void handleRunSelection(ObservableValue<? extends RunDisplayed> Observable, RunDisplayed oldVal, RunDisplayed newVal) {
        if (newVal != null) {
            if (Stream.of(RunState.FINISHED, RunState.STOPPED).anyMatch(e -> e.equals(newVal.getRunState()))) {
                new Thread(() -> endedRunGetter(newVal)).start();
            }
            else {
                EndedRuns.getItems().clear();
            }
            new Thread(() -> progressRunGetter(oldVal,newVal)).start();
            new Thread(() -> entityCountGetter(oldVal, newVal)).start();
        }
    }

    private void entityCountGetter(RunDisplayed oldVal, RunDisplayed newVal) {
        Platform.runLater(() -> {
            currentRunEntityCount.getChildren().clear();
            currentRunEntityCount.getChildren().add(new Label("loading..."));
        });
        if(oldVal!=null) oldVal.getEntityCount().hide();
        newVal.getEntityCount().Display(currentRunEntityCount);
    }

    private void progressRunGetter(RunDisplayed oldVal, RunDisplayed newVal) {
        if (oldVal!=null) oldVal.getInteractiveRun().hide();
        SplitPane node = newVal.getInteractiveRun().display();
        node.prefWidthProperty().bind(CurrentRunProgress.widthProperty());
        node.prefHeightProperty().bind(CurrentRunProgress.heightProperty());
        Platform.runLater(() -> CurrentRunProgress.setContent(node));
    }

    private void endedRunGetter(RunDisplayed run) {

        Platform.runLater(() -> {
            EndedRuns.getItems().clear();
            EndedRuns.getItems().add(new Label("loading..."));
        });

        Map<String, Map<Integer, Integer>> counts = ServerApi.getInstance().getSingleRunHistoryEntityAmount(run.getRunIdentifier());
        List<EntityChartLabel> entityLabels = counts.keySet().stream()
                .map(entity -> new EntityChartLabel(entity, counts, chart))
                .collect(Collectors.toList());

        List<Parent>  entitiesAdded = entityLabels.stream()
                .filter(e -> !run.getEntityChartLabels().contains(e))
                .collect(Collectors.toList());
        //noinspection SuspiciousMethodCalls
        List<Parent> entitiesRemoved = run.getEntityChartLabels().stream()
                .filter(e -> !entityLabels.contains(e))
                .collect(Collectors.toList());

        Map<String, Map<String, PropertyData>> histograms = ServerApi.getInstance()
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
        //noinspection SuspiciousMethodCalls
        List<Parent> propertiesRemoved = run.getEntityChartLabels().stream()
                .filter(e -> !propertyLabels.contains(e))
                .collect(Collectors.toList());

        run.removeEntityChartLabels(entitiesRemoved);
        run.addEntityChartLabels(entitiesAdded);
        run.addEntityChartLabels(propertiesAdded);
        run.removeEntityChartLabels(propertiesRemoved);

        Platform.runLater(() -> EndedRuns.setItems(run.getEntityChartLabels()));
    }

    @Override
    public void destroy() {
        historyGetter.shutdownNow();
    }
}

