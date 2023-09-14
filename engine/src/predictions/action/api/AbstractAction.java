package predictions.action.api;

import dto.subdto.show.world.action.ActionDto;
import predictions.definition.property.api.PropertyDefinition;
import predictions.definition.property.api.PropertyType;

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

    public static boolean verifyNonNumericPropertyType(PropertyDefinition<?> propertyValue) {
        return propertyValue.getType() != PropertyType.DECIMAL && propertyValue.getType() != PropertyType.FLOAT;
    }

    @Override
    public abstract ActionDto getDto();
}
