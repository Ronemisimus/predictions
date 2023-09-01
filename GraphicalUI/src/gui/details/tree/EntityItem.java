package gui.details.tree;

import dto.subdto.show.world.EntityDto;
import dto.subdto.show.world.PropertyDto;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;

public class EntityItem extends TreeItem<String> implements OpenableItem {

    private EntityDto entity;
    public EntityItem(EntityDto entity) {
        super(entity.getName(), null);
        this.entity = entity;
        entity.getProps().forEach(this::accept);
    }

    @Override
    public Parent getDetailsView() {
        Integer entityAmount = entity.getAmount();
        String entityName = entity.getName();
        Integer PropertyAmount = entity.getProps().size();

        // Create a VBox to hold the details
        VBox detailsBox = new VBox();

        // Create Labels to display the information
        Label nameLabel = new Label("Entity Name: " + entityName);
        Label amountLabel = new Label("Entity Amount: " + entityAmount);
        Label propertyAmountLabel = new Label("Property Amount: " + PropertyAmount);

        // Add Labels to the VBox
        detailsBox.getChildren().addAll(nameLabel, amountLabel, propertyAmountLabel);

        // Customize the VBox's appearance or layout if needed
        detailsBox.setSpacing(10); // Set spacing between items
        detailsBox.setStyle("-fx-padding: 10px;"); // Add padding to the VBox

        // Return the VBox as a Parent
        return detailsBox;
    }

    private void accept(PropertyDto e) {
        getChildren().add(new PropertyItem(e));
    }
}
