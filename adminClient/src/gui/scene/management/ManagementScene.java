package gui.scene.management;

import gui.scene.management.worldNameItem.WorldNameItem;
import gui.util.ServerApi;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class ManagementScene {
    @FXML
    private TitledPane titledPane;
    @FXML
    private AnchorPane toolbar;
    @FXML
    private ChoiceBox<WorldNameItem> filePathHolder;
    @FXML
    private Button loadFileButton;
    @FXML
    private Button setThreadCountButton;
    private boolean loaded;

    private ObservableList<WorldNameItem> hyperlinkList;

    @FXML
    private void initialize(){
        hyperlinkList = FXCollections.emptyObservableList();
        toolbar.prefWidthProperty().bind(titledPane.widthProperty().subtract(150));
        loadFileButton.setOnAction(this::handleLoadFileButton);
        filePathHolder.valueProperty().addListener(this::handleSelectedFileChange);
        setThreadCountButton.setOnAction(this::handleSetThreadCountButton);
        filePathHolder.setItems(hyperlinkList);
        filePathHolder.setConverter(new StringConverter<WorldNameItem>() {

            /**
             * Converts the object provided into its string form.
             * Format of the returned string is defined by the specific converter.
             *
             * @param object - a WorldNameItem containing the name and hyperlink
             * @return a string representation of the object passed in.
             */
            @Override
            public String toString(WorldNameItem object) {
                return object.getName().getText() + " " + object.getHyperlink().getText();
            }

            /**
             * Converts the string provided into an object defined by the specific converter.
             * Format of the string and type of the resulting object is defined by the specific converter.
             *
             * @param string - the text representation of the object
             * @return an object representation of the string passed in.
             */
            @Override
            public WorldNameItem fromString(String string) {
                return new WorldNameItem(string.split(" ")[0], string.split(" ")[1]);
            }
        });
        loaded = false;
    }

    private void handleSelectedFileChange(Observable observable, WorldNameItem oldValue, WorldNameItem newValue) {
        if (loaded) {
            String worldName = newValue.getName().getText();
            // TODO: show world details
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
            String worldName = ServerApi.getInstance().LoadFile(filePath);
            if (worldName != null) {
                Platform.runLater(() -> {
                    WorldNameItem worldNameItem = new WorldNameItem(worldName, filePath);
                    hyperlinkList.add(worldNameItem);
                });
            }
        }).start();
    }
}
