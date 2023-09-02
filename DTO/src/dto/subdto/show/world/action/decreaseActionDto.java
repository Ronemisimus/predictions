package dto.subdto.show.world.action;

import dto.subdto.show.world.EntityDto;

public class decreaseActionDto extends ActionDto{
    private final String propertyName;
    private final String byExpression;

    public decreaseActionDto(String propertyName,
                             String byExpression,
                             EntityDto primaryEntity,
                             EntityDto secondaryEntity) {
        super("decrease", primaryEntity, secondaryEntity);
        this.propertyName = propertyName;
        this.byExpression = byExpression;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getByExpression() {
        return byExpression;
    }
}
