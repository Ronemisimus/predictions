package gui;

import gui.details.scene.DetailsSceneController;
import gui.execution.scene.ExecutionController;
import gui.history.scene.HistoryController;
import gui.util.display.RunStateRow;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MainController {

    @FXML
    public BorderPane mainScene;
    @FXML
    private Button LoadFileButton;
    @FXML
    private Label FileLabel;
    @FXML
    private Button DetailsButton;
    @FXML
    private BorderPane centerStage;
    @FXML
    private Button newExecutionButton;
    @FXML
    private Button resultsButton;
    @FXML
    private ScrollPane QueueList;
    @FXML
    private ScrollPane mainRoot;

    // flags
    private final BooleanProperty isLoaded = new SimpleBooleanProperty(false);

    private static MainController mainController = null;
    @FXML
    private void initialize() {
        mainScene.prefWidthProperty().bind(Bindings.max(mainRoot.widthProperty().subtract(20), 1200.0));
        mainScene.prefHeightProperty().bind(Bindings.max(mainRoot.heightProperty().subtract(20), 600.0));
        LoadFileButton.setOnAction(this::handleLoadFileButtonClick);
        DetailsButton.setOnAction(event1 -> handleDetailsButtonClick());
        isLoaded.addListener(this::handleFileLoaded);
        newExecutionButton.setOnAction(event1 -> handleNewExecutionButtonClick());
        resultsButton.setOnAction(event -> handleResultsButtonClick());
        TableView<RunStateRow> table = new TableView<>();
        QueueList.setContent(table);
        table.prefHeightProperty().bind(QueueList.heightProperty().subtract(5));
        table.prefWidthProperty().bind(QueueList.widthProperty().subtract(5));
        TableColumn<RunStateRow,String> stateColumn = new TableColumn<>("State");
        stateColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.5));
        TableColumn<RunStateRow,Integer> countColumn = new TableColumn<>("Count");
        countColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.5));
        stateColumn.setCellValueFactory(cellData -> cellData.getValue().stateProperty());
        countColumn.setCellValueFactory(cellData -> cellData.getValue().countProperty());
        //noinspection unchecked
        table.getColumns().addAll(stateColumn, countColumn);
        ScheduledExecutorService runStateGetter = Executors.newScheduledThreadPool(1);
        final ObservableList<RunStateRow> rows = FXCollections.observableArrayList();
        table.setItems(rows);
        runStateGetter.scheduleAtFixedRate(() -> {
            List<RunStateRow> res = EngineApi.getInstance().getRunStates();
            List<RunStateRow> added = res.stream()
                    .filter(row -> !rows.contains(row))
                    .collect(Collectors.toList());
            List<RunStateRow> removed = rows.stream()
                    .filter(row -> !res.contains(row))
                    .collect(Collectors.toList());
            rows.addAll(added);
            rows.removeAll(removed);
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    public void handleResultsButtonClick() {
        loadSubScene("HistoryScene.fxml", HistoryController.class);
    }

    public static MainController getInstance(MainController mainController){
        if (MainController.mainController == null){
            setInstance(mainController);
        }
        return MainController.mainController;
    }

    private static void setInstance(MainController mainController){
        MainController.mainController = mainController;
    }

    private void handleFileLoaded(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (newValue)
            DetailsButton.fire();
        else {
            try {
                EngineApi.getInstance().unload();
            }catch (Exception ignored){}
            FileLabel.textProperty().set("loaded file will show here");
            centerStage.setCenter(null);
        }
    }

    @FXML
    private void handleLoadFileButtonClick(ActionEvent event) {
        isLoaded.set(false);
        isLoaded.set(EngineApi.getInstance().LoadFile(FileLabel.textProperty()));
    }

    @FXML
    private void handleDetailsButtonClick() {
        loadSubScene("DetailsScene.fxml", DetailsSceneController.class);
    }

    private void loadSubScene(String fileName, Class<?> loaderClass)
    {
        if (isLoaded.get()) {
            try {
                FXMLLoader loader = new FXMLLoader(loaderClass.getResource(fileName));
                centerStage.setCenter(null);
                Parent root = loader.load();
                centerStage.setCenter(root);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else
        {
            centerStage.setCenter(null);
        }
    }

    public BorderPane getCenterStage() {
        return centerStage;
    }

    @FXML
    public void handleNewExecutionButtonClick() {
        loadSubScene("ExecutionScene.fxml", ExecutionController.class);
    }


    public void unload() {
        isLoaded.set(false);
    }

    public void copyEnvironment(Integer identifier) {
        EngineApi.getInstance().copyEnvironment(identifier);
        Platform.runLater(() -> this.newExecutionButton.fire());
    }
}
