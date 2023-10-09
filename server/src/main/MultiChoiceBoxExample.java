package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;


public class MultiChoiceBoxExample extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Multi-Choice Box Example");

        // Create a list of custom items with name and URL
        ComboBox<CustomItem> listView = new ComboBox<>();
        listView.setCellFactory(new Callback<ListView<CustomItem>, ListCell<CustomItem>>() {
            @Override
            public ListCell<CustomItem> call(ListView<CustomItem> param) {
                return new ListCell<CustomItem>() {
                    @Override
                    protected void updateItem(CustomItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox();
                            Text text = new Text(item.getName());
                            // Create a hyperlink with the item name
                            Hyperlink hyperlink = new Hyperlink(item.getUrl());
                            hyperlink.setOnAction(event -> {
                                // Handle hyperlink click here, e.g., open the URL
                                System.out.println("Opening URL: " + item.getUrl());
                            });

                            hbox.getChildren().addAll(text, hyperlink);
                            hbox.setAlignment(Pos.CENTER);
                            // Set the hyperlink as the graphic of the list cell
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });

        // Add some custom items to the list
        listView.getItems().addAll(
                new CustomItem("Google", "https://www.google.com"),
                new CustomItem("Yahoo", "https://www.yahoo.com"),
                new CustomItem("Bing", "https://www.bing.com")
        );

        VBox vbox = new VBox(listView);
        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Platform.runLater(() -> listView.getItems().add(new CustomItem("blaaa", "https://www.blaaa.com")));
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Custom class to represent items with name and URL
    private static class CustomItem {
        private final String name;
        private final String url;

        public CustomItem(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        @Override
        public String toString() {
            return name + " " + url;
        }
    }
}

