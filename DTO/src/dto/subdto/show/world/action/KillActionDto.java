package dto.subdto.show.world.action;

import dto.subdto.show.world.EntityDto;

public class KillActionDto extends ActionDto {

    public KillActionDto(EntityDto primaryEntity,
                         EntityDto secondaryEntity) {
        super("Kill", primaryEntity, secondaryEntity);
    }
}
