package dto.subdto;

import dto.DTO;
import dto.subdto.show.instance.PropertyInstanceDto;
import dto.subdto.show.world.PropertyDto;

import java.util.List;

public class InitializeDto implements DTO {
    private final List<PropertyInstanceDto> environment;

    public InitializeDto(List<PropertyInstanceDto> environment) {
        this.environment = environment;
    }

    public List<PropertyInstanceDto> getEnvironment() {
        return environment;
    }
}
