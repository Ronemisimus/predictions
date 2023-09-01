package gui.details.tree;

import dto.subdto.show.world.PropertyDto;
import javafx.scene.Parent;
import javafx.scene.control.TreeItem;

import java.util.List;

public class EnvItem extends TreeItem<String> {

    private List<PropertyDto> environment;
    public EnvItem(List<PropertyDto> environment) {
        super("environment", null);
        this.environment = environment;
        environment.forEach(this::accept);
    }

    private void accept(PropertyDto prop) {
        getChildren().add(new PropertyItem(prop));
    }
}
