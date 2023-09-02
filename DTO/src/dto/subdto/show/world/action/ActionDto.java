package dto.subdto.show.world.action;

import dto.subdto.show.world.EntityDto;

public class ActionDto {
    private final String type;
    private final EntityDto primaryEntity;

    private final EntityDto secondaryEntity;

    public ActionDto(String type,
                     EntityDto primaryEntity,
                     EntityDto secondaryEntity) {
        this.type = type;
        this.primaryEntity = primaryEntity;
        this.secondaryEntity = secondaryEntity;
    }
    public String getType() {
        return type;
    }

    public EntityDto getPrimaryEntity() {
        return primaryEntity;
    }

    public EntityDto getSecondaryEntity() {
        return secondaryEntity;
    }
}
