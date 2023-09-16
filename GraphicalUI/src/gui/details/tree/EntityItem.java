package gui.details.tree;

import dto.subdto.show.world.EntityDto;
import dto.subdto.show.world.PropertyDto;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;

public class EntityItem extends TreeItem<String> implements OpenableItem {

    private final EntityDto entity;
    private Integer entityAmount;
    public EntityItem(EntityDto entity) {
        super(entity.getName(), null);
        this.entity = entity;
        entityAmount = entity.getAmount();
        entity.getProps().forEach(this::accept);
    }

    @Override
    public Parent getDetailsView() {
        String entityName = entity.getName();
        int PropertyAmount = entity.getProps().size();

        // Create a VBox to hold the details
        VBox detailsBox = new VBox();

        // Create Labels to display the information
        Label nameLabel = new Label("Entity Name: " + entityName);
        Label amountLabel = new Label("Entity Amount: " + entityAmount);
        Label propertyAmountLabel = new Label("Property Amount: " + PropertyAmount);

        // Add Labels to the VBox
        detailsBox.getChildren().addAll(nameLabel, amountLabel, propertyAmountLabel);

        // Return the VBox as a Parent
        return detailsBox;
    }

    public Integer getEntityAmount() {
        return entityAmount;
    }

    public void setEntityAmount(Integer entityAmount) {
        this.entityAmount = entityAmount;
    }

    private void accept(PropertyDto e) {
        getChildren().add(new PropertyItem(e));
    }
}
