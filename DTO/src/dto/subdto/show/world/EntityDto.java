package dto.subdto.show.world;

import java.util.List;

public class EntityDto {
    private final List<PropertyDto> props;
    private final String name;

    public EntityDto(List<PropertyDto> props, String name) {
        this.props = props;
        this.name = name;
    }

    public List<PropertyDto> getProps() {
        return props;
    }

    public String getName() {
        return name;
    }
}
