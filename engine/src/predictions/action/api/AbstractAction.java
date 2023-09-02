package predictions.action.api;

import dto.subdto.show.world.action.ActionDto;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.property.api.PropertyType;
import predictions.execution.instance.property.PropertyInstance;

import java.util.ArrayList;

public abstract class AbstractAction implements Action {

    private final ActionType actionType;
    private final ContextDefinition contextDefinition;

    protected AbstractAction(ActionType actionType, ContextDefinition contextDefinition) {
        this.actionType = actionType;
        this.contextDefinition = contextDefinition;
    }

    @Override
    public ActionType getActionType() {
        return actionType;
    }

    @Override
    public ContextDefinition getContextDefinition() {
        return contextDefinition;
    }

    public static boolean verifyNonNumericPropertyType(PropertyInstance<?> propertyValue) {
        return
                !PropertyType.DECIMAL.equals(propertyValue.getPropertyDefinition().getType()) && !PropertyType.FLOAT.equals(propertyValue.getPropertyDefinition().getType());
    }

    @Override
    public abstract ActionDto getDto();
}
