package dto;

import dto.subdto.show.world.WorldDto;

public class ShowWorldDto implements DTO{
    private final WorldDto world;

    public ShowWorldDto(WorldDto world) {
        this.world = world;
    }

    public WorldDto getWorld() {
        return world;
    }
}
