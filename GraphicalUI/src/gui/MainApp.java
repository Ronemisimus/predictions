package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mainApp.fxml"));
            Parent root = loader.load();
            MainController.getInstance(loader.getController());
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Predictions");
            primaryStage.setOnCloseRequest(e -> {
                EngineApi.getInstance().unload();
                System.exit(0);
            });
            primaryStage.show();
        }catch (Exception e)
        {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
