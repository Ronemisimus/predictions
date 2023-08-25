package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // load fxml file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/mainApp.fxml"));
        try {
            primaryStage.setScene(new Scene(loader.load()));
            primaryStage.setTitle("prediction");
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
