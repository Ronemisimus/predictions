package gui.scene.management.tree;

import dto.subdto.show.world.PropertyDto;
import javafx.scene.control.TreeItem;

import java.util.List;

public class EnvItem extends TreeItem<String> {

    public EnvItem(List<PropertyDto> environment) {
        super("environment", null);
        environment.forEach(this::accept);
    }

    private void accept(PropertyDto prop) {
        getChildren().add(new PropertyItem(prop));
    }
}
