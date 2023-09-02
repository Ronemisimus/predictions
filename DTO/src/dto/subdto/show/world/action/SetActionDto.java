package dto.subdto.show.world.action;

import dto.subdto.show.world.EntityDto;

public class SetActionDto extends ActionDto {
    private final String propertyName;
    private final String valueExpression;

    public SetActionDto(EntityDto primaryEntity,
                        EntityDto secondaryEntity,
                        String propertyName,
                        String valueExpression) {
        super("Set", primaryEntity, secondaryEntity);
        this.propertyName = propertyName;
        this.valueExpression = valueExpression;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getValueExpression() {
        return valueExpression;
    }
}
