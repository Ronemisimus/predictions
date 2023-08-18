package predictions.action.api;

import predictions.definition.entity.EntityDefinition;
import predictions.definition.property.api.PropertyType;
import predictions.execution.instance.property.PropertyInstance;

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
}
