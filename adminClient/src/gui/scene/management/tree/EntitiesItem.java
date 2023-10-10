package gui.scene.management.tree;

import dto.subdto.show.world.EntityDto;
import javafx.scene.control.TreeItem;

import java.util.List;

public class EntitiesItem extends TreeItem<String> {

    public EntitiesItem(List<EntityDto> entities) {
        super("entities", null);
        entities.forEach(this::accept);
    }

    private void accept(EntityDto entity) {
        getChildren().add(new EntityItem(entity));
    }
}
