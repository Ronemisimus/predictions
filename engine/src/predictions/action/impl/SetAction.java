package predictions.action.impl;

import predictions.action.api.AbstractAction;
import predictions.action.api.ActionType;
import predictions.definition.entity.EntityDefinition;
import predictions.execution.context.Context;
import predictions.execution.instance.property.PropertyInstance;
import predictions.expression.api.Expression;
import predictions.expression.impl.DoubleComplexExpression;

public class SetAction extends AbstractAction {

    private final String property;
    private Expression<Double> valueExpression;

    public SetAction(EntityDefinition entityDefinition,
                     String property,
                     String valueExpression) {
        super(ActionType.SET, entityDefinition);
        this.property = property;
        this.valueExpression = new DoubleComplexExpression(valueExpression);
    }
    @Override
    public void invoke(Context context) {
        PropertyInstance propertyInstance = context.getPrimaryEntityInstance().getPropertyByName(property);
        if (!verifyNumericPropertyType(propertyInstance)) {
            throw new IllegalArgumentException("increase action can't operate on a none number property [" + property);
        }

        // TODO: get world ticks
        int world_time = 500;

        propertyInstance.updateValue(valueExpression.evaluate(context), world_time);
    }
}
