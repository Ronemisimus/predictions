package dto.subdto.show.instance;

import dto.DTO;
import dto.subdto.show.world.PropertyDto;

public class PropertyInstanceDto implements DTO {
    private final PropertyDto def;
    private final Comparable<?> value;

    public PropertyInstanceDto(PropertyDto def, Comparable<?> value) {
        this.def = def;
        this.value = value;
    }

    public PropertyDto getDef() {
        return def;
    }

    public Comparable<?> getValue() {
        return value;
    }
}
