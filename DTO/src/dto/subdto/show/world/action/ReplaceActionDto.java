package dto.subdto.show.world.action;

import dto.subdto.show.world.EntityDto;

public class ReplaceActionDto extends ActionDto{
    private final String mode;

    public ReplaceActionDto(EntityDto killedEntity,
                            EntityDto bornEntity,
                            String mode) {
        super("Replace", killedEntity, bornEntity);
        this.mode = mode;
    }

    public EntityDto getKilledEntity() {
        return getPrimaryEntity();
    }

    public EntityDto getBornEntity() {
        return getSecondaryEntity();
    }

    public String getMode() {
        return mode;
    }
}
