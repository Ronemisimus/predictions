package clientGui;

import dto.subdto.requests.RequestDetailsDto;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import clientGui.scene.newExecution.RequestSelection;

public class testComboBoxRequestSelection extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("ComboBox Example");

        // Create a list of RequestSelection items
        ObservableList<RequestSelection> requestList = FXCollections.observableArrayList(
                new RequestSelection(new RequestDetailsDto.Builder()
                        .requestId(1)
                        .worldName("World 1").build()),
                new RequestSelection(new RequestDetailsDto.Builder()
                        .requestId(2)
                        .worldName("World 2").build()),
                new RequestSelection(new RequestDetailsDto.Builder()
                        .requestId(3)
                        .worldName("World 3").build())
        );

        // Create the ComboBox
        ComboBox<RequestSelection> requestComboBox = new ComboBox<>(requestList);
        requestComboBox.setPromptText("Select a request");

        // Create a VBox to hold the ComboBox
        VBox vbox = new VBox(requestComboBox);

        // Create a scene and set it on the stage
        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);

        primaryStage.show();
    }
}

