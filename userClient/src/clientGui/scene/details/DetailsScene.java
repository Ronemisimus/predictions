package clientGui.scene.details;

import clientGui.scene.details.tree.OpenableItem;
import clientGui.util.ServerApi;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class DetailsScene {
    @FXML
    private AnchorPane toolbar;
    @FXML
    private TitledPane titledPane;
    @FXML
    private ComboBox<String> nameSelector;
    @FXML
    private TreeView<String> treeView;
    @FXML
    private ScrollPane detailsView;
    @FXML
    private void initialize(){
        toolbar.prefWidthProperty().bind(titledPane.widthProperty().subtract(150));
        nameSelector.valueProperty().addListener(this::handleNameSelector);
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(this::updateNameSelector, 0, 500, TimeUnit.MILLISECONDS);
    }

    private void updateNameSelector() {
        ObservableList<String> names = nameSelector.getItems();

        List<String> loadedWorlds = ServerApi.getInstance().getLoadedWorlds();

        List<String> addedNames = loadedWorlds.stream()
                .filter(name -> !names.contains(name))
                .collect(Collectors.toList());
        List<String> removedNames = names.stream()
                .filter(name -> !loadedWorlds.contains(name))
                .collect(Collectors.toList());
        Platform.runLater(() -> {
            names.addAll(addedNames);
            names.removeAll(removedNames);
        });
    }

    private void handleNameSelector(Observable observable, String ignoredOldValue, String newValue) {
        if (newValue != null) {
            TreeItem<String> res = ServerApi.getInstance().showLoadedWorld(newValue);
            treeView.setRoot(res);

            treeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            treeView.setCellFactory(param -> getTreeCell());
        }
    }

    @NotNull
    private TreeCell<String> getTreeCell() {
        return new TreeCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item);
                }

                selectedProperty().addListener(e -> {
                    if (((ReadOnlyBooleanProperty) e).getValue()) {
                        if (getTreeItem() instanceof OpenableItem) {
                            OpenableItem openableItem = (OpenableItem) getTreeItem();
                            Parent root = openableItem.getDetailsView();
                            if (root instanceof VBox) {
                                VBox vBox = (VBox) root;
                                vBox.setSpacing(10);
                                vBox.setStyle("-fx-padding: 10px;");
                            }
                            detailsView.setContent(root);
                        } else {
                            detailsView.setContent(null);
                        }
                    }
                });
            }
        };
    }
}
