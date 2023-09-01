package gui;

import gui.details.scene.DetailsSceneController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {


    // fxml controls
    @FXML
    private Button LoadFileButton;
    @FXML
    private Label FileLabel;
    @FXML
    private Button DetailsButton;
    @FXML
    private BorderPane centerStage;


    // flags
    private final BooleanProperty isLoaded = new SimpleBooleanProperty(false);

    private static MainController mainController = null;
    @FXML
    private void initialize() {
        LoadFileButton.setOnAction(this::handleLoadFileButtonClick);
        DetailsButton.setOnAction(this::handleDetailsButtonClick);
        isLoaded.addListener(this::handleFileLoaded);
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
        DetailsButton.fire();
    }

    @FXML
    private void handleLoadFileButtonClick(ActionEvent event) {
        isLoaded.set(EngineApi.getInstance().LoadFile(FileLabel.textProperty()));
    }

    @FXML
    private void handleDetailsButtonClick(ActionEvent event) {
        if (isLoaded.get()) {
            try {
                FXMLLoader loader = new FXMLLoader(DetailsSceneController.class.getResource("DetailsScene.fxml"));
                Parent root = loader.load();
                centerStage.setCenter(root);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
