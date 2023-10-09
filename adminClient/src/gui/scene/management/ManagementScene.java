package gui.scene.management;

import gui.scene.management.worldNameItem.WorldNameItem;
import gui.util.ServerApi;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class ManagementScene {
    @FXML
    private TitledPane titledPane;
    @FXML
    private AnchorPane toolbar;
    @FXML
    private ComboBox<WorldNameItem> nameSelector;
    @FXML
    private Hyperlink filePathHolder;
    @FXML
    private Button loadFileButton;
    @FXML
    private Button setThreadCountButton;

    @FXML
    private void initialize(){
        toolbar.prefWidthProperty().bind(titledPane.widthProperty().subtract(150));
        loadFileButton.setOnAction(this::handleLoadFileButton);
        setThreadCountButton.setOnAction(this::handleSetThreadCountButton);
        nameSelector.setCellFactory(comboBox -> new ListCell<WorldNameItem>() {
            @Override
            public void updateItem(WorldNameItem item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getName());
                }
            }
        });
        filePathHolder.tooltipProperty().bind(Bindings.createObjectBinding(
                () -> new Tooltip(filePathHolder.getText()),
                filePathHolder.textProperty()
        ));
        nameSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                filePathHolder.setText(newValue.getHyperlink());
            }
        });
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
            fileChooser.setInitialDirectory(new File(".").getCanonicalFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String filePath = Optional.ofNullable(fileChooser.showOpenDialog(null)).orElse(new File("")).getAbsolutePath();

        new Thread(() -> {
            String worldName = ServerApi.getInstance().LoadFile(filePath);
            if (worldName != null) {
                Platform.runLater(() -> {
                    WorldNameItem worldNameItem = new WorldNameItem(worldName, filePath);
                    nameSelector.getItems().add(worldNameItem);
                });
            }
        }).start();
    }
}
