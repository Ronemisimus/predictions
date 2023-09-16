package gui;

import gui.details.scene.DetailsSceneController;
import gui.execution.scene.ExecutionController;
import gui.history.scene.HistoryController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

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


    // flags
    private final BooleanProperty isLoaded = new SimpleBooleanProperty(false);

    private static MainController mainController = null;
    @FXML
    private void initialize() {
        LoadFileButton.setOnAction(this::handleLoadFileButtonClick);
        DetailsButton.setOnAction(event1 -> handleDetailsButtonClick());
        isLoaded.addListener(this::handleFileLoaded);
        newExecutionButton.setOnAction(event1 -> handleNewExecutionButtonClick());
        resultsButton.setOnAction(event -> handleResultsButtonClick());
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
            EngineApi.getInstance().unload();
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

    @FXML
    public void handleNewExecutionButtonClick() {
        loadSubScene("ExecutionScene.fxml", ExecutionController.class);
    }


    public void unload() {
        isLoaded.set(false);
        EngineApi.getInstance().unload();
    }
}
