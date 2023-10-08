package gui.scene.management;

import gui.util.ServerApi;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class ManagementScene {
    @FXML
    private TitledPane titledPane;
    @FXML
    private AnchorPane toolbar;
    @FXML
    private ChoiceBox<Hyperlink> filePathHolder;
    @FXML
    private Button loadFileButton;
    @FXML
    private Button setThreadCountButton;
    private boolean loaded;

    private ObservableList<Hyperlink> hyperlinkList;

    @FXML
    private void initialize(){
        hyperlinkList = new SimpleListProperty<>();
        toolbar.prefWidthProperty().bind(titledPane.widthProperty().subtract(150));
        loadFileButton.setOnAction(this::handleLoadFileButton);
        filePathHolder.valueProperty().addListener(this::handleSelectedFileChange);
        setThreadCountButton.setOnAction(this::handleSetThreadCountButton);
        filePathHolder.tooltipProperty()
                .bind(Bindings.createObjectBinding(
                        () -> new Tooltip(filePathHolder.valueProperty().get().getText())
                        ,filePathHolder.valueProperty()));
        filePathHolder.setItems(hyperlinkList);
        loaded = false;
    }

    private void handleSelectedFileChange(Observable observable, Hyperlink oldValue, Hyperlink newValue) {
        if (loaded) {
            String filePath = newValue.getText();
        }
    }

    private void handleFilePathHolderClick(ActionEvent actionEvent) {
        if (loaded) {
            String filePath = filePathHolder.valueProperty().get().getText();
            new Thread(() -> {
                // Check if Desktop is supported (i.e., the application is running in a GUI environment)
                if (Desktop.isDesktopSupported()) {
                    Desktop desktop = Desktop.getDesktop();
                    File fileToOpen = new File(filePath);

                    try {
                        // Check if the file/folder exists before attempting to open it
                        if (fileToOpen.exists()) {
                            if (desktop.isSupported(Desktop.Action.OPEN)) {
                                desktop.open(fileToOpen);
                            } else {
                                System.err.println("Desktop is not supported.");
                            }
                        } else {
                            System.out.println("File or folder does not exist: " + filePath);
                        }
                    } catch (IOException e) {
                        System.err.println("Error opening file/folder: " + filePath);
                        e.printStackTrace(System.err);
                    }
                } else {
                    System.err.println("Desktop is not supported.");
                }
            }).start();
        }
    }

    private void handleSetThreadCountButton(ActionEvent actionEvent) {
        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle("Set Thread Count");
        textInputDialog.setHeaderText("Please enter the thread count");
        textInputDialog.setContentText("Thread Count:");
        Optional<String> result = textInputDialog.showAndWait();
        if (result.isPresent() && result.get().matches("\\d+") && Integer.parseInt(result.get())>0) {
            int enteredInteger = Integer.parseInt(result.get());

            ServerApi.getInstance().SetThreadCount(enteredInteger);

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("Please enter a number greater then 0");
            alert.showAndWait();
        }
    }

    private void handleLoadFileButton(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load XML File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML Files", "*.xml")
        );
        // open project root
        try {
            loaded = true;
            fileChooser.setInitialDirectory(new File(".").getCanonicalFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String filePath = fileChooser.showOpenDialog(null).getAbsolutePath();

        new Thread(() -> {
            if (ServerApi.getInstance().LoadFile(filePath)) {
                Platform.runLater(() -> {
                    Hyperlink temp = new Hyperlink(filePath);
                    temp.setOnAction(this::handleFilePathHolderClick);
                    hyperlinkList.add(temp);
                });
            }
        }).start();
    }
}
