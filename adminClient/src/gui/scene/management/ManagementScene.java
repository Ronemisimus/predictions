package gui.scene.management;

import gui.scene.management.tree.OpenableItem;
import gui.scene.management.worldNameItem.WorldNameItem;
import gui.util.ServerApi;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    private TreeView<String> treeView;
    @FXML
    private ScrollPane detailsView;
    @FXML
    private ScrollPane queueList;

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
                TreeItem<String> res = ServerApi.getInstance().showLoadedWorld(newValue.getName());
                treeView.setRoot(res);

                treeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
                treeView.setCellFactory(param -> new TreeCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(item);
                        }

                        selectedProperty().addListener(e -> {
                            if (((ReadOnlyBooleanProperty)e).getValue()) {
                                if (getTreeItem() instanceof OpenableItem) {
                                    OpenableItem openableItem = (OpenableItem) getTreeItem();
                                    Parent root = openableItem.getDetailsView();
                                    if (root instanceof VBox) {
                                        ((VBox) root).setSpacing(10);
                                        root.setStyle("-fx-padding: 10px;");
                                    }
                                    detailsView.setContent(root);
                                } else {
                                    detailsView.setContent(null);
                                }
                            }
                        });
                    }
                });

            }
        });
        filePathHolder.setOnAction(this::handleHyperlinkClick);
        initQueueList();
    }

    private void initQueueList() {
        TableView<RunStateRow> table = new TableView<>();
        queueList.setContent(table);
        table.prefHeightProperty().bind(queueList.heightProperty().subtract(5));
        table.prefWidthProperty().bind(queueList.widthProperty().subtract(5));
        TableColumn<RunStateRow,String> stateColumn = new TableColumn<>("State");
        stateColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.5));
        TableColumn<RunStateRow,Integer> countColumn = new TableColumn<>("Count");
        countColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.5));
        stateColumn.setCellValueFactory(cellData -> cellData.getValue().stateProperty());
        countColumn.setCellValueFactory(cellData -> cellData.getValue().countProperty());
        //noinspection unchecked
        table.getColumns().addAll(stateColumn, countColumn);
        ScheduledExecutorService runStateGetter = Executors.newScheduledThreadPool(1);
        final ObservableList<RunStateRow> rows = FXCollections.observableArrayList();
        table.setItems(rows);
        runStateGetter.scheduleAtFixedRate(() -> {
            java.util.List<RunStateRow> res = ServerApi.getInstance().getRunStates();
            java.util.List<RunStateRow> added = res.stream()
                    .filter(row -> !rows.contains(row))
                    .collect(Collectors.toList());
            List<RunStateRow> removed = rows.stream()
                    .filter(row -> !res.contains(row))
                    .collect(Collectors.toList());
            rows.addAll(added);
            rows.removeAll(removed);
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    private void handleHyperlinkClick(ActionEvent actionEvent) {
        String filePath = filePathHolder.getText();
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
