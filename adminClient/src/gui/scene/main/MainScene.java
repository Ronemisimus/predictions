package gui.scene.main;

import gui.history.scene.HistoryController;
import gui.scene.SceneController;
import gui.scene.allocations.Allocations;
import gui.scene.management.ManagementScene;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainScene{
    @FXML
    private ScrollPane mainRoot;
    @FXML
    private BorderPane content;
    @FXML
    private BorderPane centerStage;
    @FXML
    private Button managementButton;
    @FXML
    private Button allocationsButton;
    @FXML
    private Button executionHistoryButton;
    private SceneController currentController;

    @FXML
    public void initialize(){
        managementButton.setOnAction(this::handleManagementButton);
        allocationsButton.setOnAction(this::handleAllocationsButton);
        executionHistoryButton.setOnAction(this::handleExecutionHistoryButton);
        content.prefWidthProperty().bind(Bindings.max(900, mainRoot.widthProperty().subtract(20)));
        content.prefHeightProperty().bind(Bindings.max(600, mainRoot.heightProperty().subtract(20)));
        Platform.runLater(()->managementButton.fire());
    }

    @FXML
    private void handleManagementButton(ActionEvent actionEvent) {
        loadSubScene("managementScene.fxml", ManagementScene.class);
        currentController = ManagementScene.getInstance();
    }

    @FXML
    public void handleAllocationsButton(ActionEvent actionEvent){
        loadSubScene("allocations.fxml", Allocations.class);
        currentController = Allocations.getInstance();
    }

    @FXML
    public void handleExecutionHistoryButton(ActionEvent actionEvent){
        loadSubScene("HistoryScene.fxml", HistoryController.class);
        currentController = HistoryController.getInstance();
    }

    private void loadSubScene(String fileName, Class<?> loaderClass)
    {
        if (currentController != null) {
            currentController.destroy();
        }
        try {
            FXMLLoader loader = new FXMLLoader(loaderClass.getResource(fileName));
            centerStage.setCenter(null);
            AnchorPane root = loader.load();
            centerStage.setCenter(root);
            root.prefWidthProperty().bind(centerStage.widthProperty());
            root.prefHeightProperty().bind(centerStage.heightProperty());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
