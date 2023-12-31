package dto.subdto.show.world.action;

import dto.subdto.show.world.EntityDto;

import java.util.List;

public class ProximityActionDto extends ActionDto {
    private final String ofValue;
    private final List<ActionDto> actions;

    public ProximityActionDto(EntityDto sourceEntity,
                              EntityDto targetEntity,
                              String ofValue,
                              List<ActionDto> actions) {
        super("Proximity", sourceEntity, targetEntity);
        this.ofValue = ofValue;
        this.actions = actions;
    }

    public EntityDto getSourceEntity() {
        return getPrimaryEntity();
    }

    public EntityDto getTargetEntity() {
        return getSecondaryEntity();
    }

    public String getOfValue() {
        return ofValue;
    }

    public List<ActionDto> getActions() {
        return actions;
    }
}
