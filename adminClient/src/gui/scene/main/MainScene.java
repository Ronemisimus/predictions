package gui.scene.main;

import gui.scene.management.ManagementScene;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
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

    private static MainScene mainController = null;

    @FXML
    public void initialize(){
        managementButton.setOnAction(this::handleManagementButton);
        allocationsButton.setOnAction(this::handleAllocationsButton);
        executionHistoryButton.setOnAction(this::handleExecutionHistoryButton);
        content.prefWidthProperty().bind(Bindings.max(900, mainRoot.widthProperty().subtract(20)));
        content.prefHeightProperty().bind(Bindings.max(600, mainRoot.heightProperty().subtract(20)));
        new Thread(() -> getInstance(this)).start();
        Platform.runLater(()->managementButton.fire());
    }

    public static synchronized MainScene getInstance(MainScene mainScene) {
        if(mainController==null){
            mainController = mainScene;
        }
        return mainScene;
    }

    @FXML
    private void handleManagementButton(ActionEvent actionEvent) {
        loadSubScene("managementScene.fxml", ManagementScene.class);
    }

    @FXML
    public void handleAllocationsButton(ActionEvent actionEvent){

    }

    @FXML
    public void handleExecutionHistoryButton(ActionEvent actionEvent){

    }

    private void loadSubScene(String fileName, Class<?> loaderClass)
    {
        try {
            FXMLLoader loader = new FXMLLoader(loaderClass.getResource(fileName));
            centerStage.setCenter(null);
            Parent root = loader.load();
            centerStage.setCenter(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
