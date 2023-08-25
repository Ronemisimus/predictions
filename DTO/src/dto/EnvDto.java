package dto;

import dto.subdto.show.world.PropertyDto;

import java.util.List;

public class EnvDto implements DTO{
    private final List<PropertyDto> environment;

    public EnvDto(List<PropertyDto> environment) {
        this.environment = environment;
    }

    public List<PropertyDto> getEnvironment() {
        return environment;
    }
}
