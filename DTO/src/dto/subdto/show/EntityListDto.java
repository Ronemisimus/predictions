package dto.subdto.show;

import dto.DTO;
import dto.subdto.show.world.EntityDto;

import java.util.List;

public class EntityListDto implements DTO {
    private final List<EntityDto> entities;

    public EntityListDto(List<EntityDto> entities) {
        this.entities = entities;
    }

    public List<EntityDto> getEntities() {
        return entities;
    }
}
