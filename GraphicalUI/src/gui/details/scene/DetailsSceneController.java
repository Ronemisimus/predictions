package gui.details.scene;

import gui.EngineApi;
import gui.details.tree.OpenableItem;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane;

import java.awt.event.MouseEvent;
import java.beans.EventHandler;

public class DetailsSceneController {
    @FXML
    private TreeView<String> treeView;
    @FXML
    private ScrollPane detailView;

    @FXML
    public void initialize() {
        TreeItem<String> res = EngineApi.getInstance().showLoadedWorld();
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
                    setOnMouseClicked(e -> {
                        if(getTreeItem() instanceof OpenableItem)
                        {
                            OpenableItem openableItem = (OpenableItem) getTreeItem();
                            detailView.setContent(openableItem.getDetailsView());
                        }
                        else
                        {
                            detailView.setContent(null);
                        }
                    });
                }
            }
        });
    }


}
