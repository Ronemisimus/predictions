package predictions.action.api;

import dto.subdto.show.world.action.ActionDto;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.property.api.PropertyType;
import predictions.execution.instance.property.PropertyInstance;

import java.util.ArrayList;

public abstract class AbstractAction implements Action {

    private final ActionType actionType;
    private final EntityDefinition entityDefinition;

    protected AbstractAction(ActionType actionType, EntityDefinition entityDefinition) {
        this.actionType = actionType;
        this.entityDefinition = entityDefinition;
    }

    @Override
    public ActionType getActionType() {
        return actionType;
    }

    @Override
    public EntityDefinition getContextEntity() {
        return entityDefinition;
    }

    public static boolean verifyNonNumericPropertyType(PropertyInstance<?> propertyValue) {
        return
                !PropertyType.DECIMAL.equals(propertyValue.getPropertyDefinition().getType()) && !PropertyType.FLOAT.equals(propertyValue.getPropertyDefinition().getType());
    }

    @Override
    public ActionDto getDto() {
        return new ActionDto(new ArrayList<>(),
                getActionType().name() + " on " + getContextEntity().getName());
    }
}
