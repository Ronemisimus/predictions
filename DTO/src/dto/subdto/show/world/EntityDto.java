package dto.subdto.show.world;

import java.util.List;

public class EntityDto {
    private final List<PropertyDto> props;
    private final String name;
    private final Integer amount;

    public EntityDto(List<PropertyDto> props, String name, Integer amount) {
        this.props = props;
        this.name = name;
        this.amount = amount;
    }

    public List<PropertyDto> getProps() {
        return props;
    }

    public String getName() {
        return name;
    }

    public Integer getAmount() {
        return amount;
    }
}
