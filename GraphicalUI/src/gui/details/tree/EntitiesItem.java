package gui.details.tree;

import dto.subdto.show.world.EntityDto;
import javafx.scene.control.TreeItem;

import java.util.List;

public class EntitiesItem extends TreeItem<String> {

    private List<EntityDto> entities;
    public EntitiesItem(List<EntityDto> entities) {
        super("entities", null);
        this.entities = entities;
        entities.forEach(this::accept);
    }

    private void accept(EntityDto entity) {
        getChildren().add(new EntityItem(entity));
    }
}
