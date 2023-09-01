package gui.details.tree.action;

import dto.subdto.show.world.ActionDto;
import gui.details.tree.OpenableItem;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;

public class ActionItem extends TreeItem<String> implements OpenableItem {
    private ActionDto action;
    public ActionItem(ActionDto action) {
        super(action.getName(), null);
        this.action = action;
    }

    @Override
    public Parent getDetailsView() {
        VBox detailsBox = new VBox();

        Label nameLabel = new Label("Action Name: " + action.getName());
        detailsBox.getChildren().addAll(nameLabel);

        // Customize the VBox's appearance or layout if needed
        detailsBox.setSpacing(10); // Set spacing between items
        detailsBox.setStyle("-fx-padding: 10px;"); // Add padding to the VBox

        return detailsBox;
    }
}
