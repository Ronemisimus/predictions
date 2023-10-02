package gui.scene.main;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;

public class MainScene{
    @FXML
    private ScrollPane mainRoot;
    @FXML
    private BorderPane content;
    @FXML
    private BorderPane centerStage;
    @FXML
    private Button DetailsButton;
    @FXML
    private Button newExecutionButton;
    @FXML
    private Button resultsButton;

    @FXML
    public void initialize(){
        DetailsButton.setOnAction(this::handleDetailsButton);
        newExecutionButton.setOnAction(this::handleNewExecutionButton);
        resultsButton.setOnAction(this::handleResultsButton);
        content.prefWidthProperty().bind(Bindings.max(900, mainRoot.widthProperty().subtract(20)));
        content.prefHeightProperty().bind(Bindings.max(600, mainRoot.heightProperty().subtract(20)));
    }

    @FXML
    private void handleDetailsButton(ActionEvent actionEvent) {
    }

    @FXML
    public void handleNewExecutionButton(ActionEvent actionEvent){

    }

    @FXML
    public void handleResultsButton(ActionEvent actionEvent){

    }
}
