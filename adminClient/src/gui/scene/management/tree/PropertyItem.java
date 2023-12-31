package gui.scene.management.tree;

import dto.subdto.show.world.PropertyDto;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;

public class PropertyItem extends TreeItem<String> implements OpenableItem {
    private final PropertyDto prop;
    public PropertyItem(PropertyDto e) {
        super(e.getName(), null);
        this.prop = e;
    }

    @Override
    public Parent getDetailsView() {
        VBox detailsBox = new VBox();

        Label nameLabel = new Label("Property Name: " + prop.getName());
        Label typeLabel = new Label("Property Type: " + prop.getType());
        Label randInitLabel = new Label("Random Initialization is " + (prop.isRandomInit()? "enabled" : "disabled"));
        Label initValueLabel = new Label("initial value if not random: \"" + prop.getInitValue()+"\"");
        detailsBox.getChildren().addAll(nameLabel, typeLabel, randInitLabel, initValueLabel);

        if (prop.getFrom() != null || prop.getTo() != null)
        {
            Label range = new Label("Range: " + (prop.getFrom()==null? "-" : prop.getFrom()) + " to " + (prop.getTo()==null? "-" : prop.getTo()));
            detailsBox.getChildren().add(range);
        }

        return detailsBox;
    }
}
