package clientGui.scene.main;

import clientGui.scene.details.DetailsScene;
import clientGui.scene.requests.Requests;
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
    private Button detailsButton;
    @FXML
    private Button requestsButton;
    @FXML
    private Button newExecutionButton;
    @FXML
    private Button resultsButton;

    private static MainScene mainController = null;

    @FXML
    public void initialize(){
        detailsButton.setOnAction(this::handleDetailsButton);
        requestsButton.setOnAction(this::handleRequestsButton);
        newExecutionButton.setOnAction(this::handleNewExecutionButton);
        resultsButton.setOnAction(this::handleResultsButton);
        content.prefWidthProperty().bind(Bindings.max(900, mainRoot.widthProperty().subtract(20)));
        content.prefHeightProperty().bind(Bindings.max(600, mainRoot.heightProperty().subtract(20)));
        new Thread(() -> getInstance(this)).start();
        Platform.runLater(()->detailsButton.fire());
    }

    private void handleNewExecutionButton(ActionEvent actionEvent) {
        // TODO: handle new execution button
    }

    public static synchronized MainScene getInstance(MainScene mainScene) {
        if(mainController==null){
            mainController = mainScene;
        }
        return mainScene;
    }

    @FXML
    private void handleDetailsButton(ActionEvent actionEvent) {
        loadSubScene("DetailsScene.fxml", DetailsScene.class);
    }

    @FXML
    public void handleRequestsButton(ActionEvent actionEvent){
        loadSubScene("requests.fxml", Requests.class);
    }

    @FXML
    public void handleResultsButton(ActionEvent actionEvent){
        // TODO: handle results button
    }

    private void loadSubScene(String fileName, Class<?> loaderClass)
    {
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
