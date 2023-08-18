package predictions.action.impl;

import predictions.action.api.AbstractAction;
import predictions.action.api.ActionType;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.property.api.PropertyType;
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
        PropertyInstance<?> propertyInstance = context.getPrimaryEntityInstance().getPropertyByName(property);
        if (verifyNonNumericPropertyType(propertyInstance)) {
            throw new IllegalArgumentException("increase action can't operate on a none number property [" + property);
        }

        Comparable<Double> expVal = valueExpression.evaluate(context);

        int world_time = context.getTick();

        if (propertyInstance.getPropertyDefinition().getType() == PropertyType.DECIMAL) {
            PropertyInstance<Integer> prop = (PropertyInstance<Integer>) propertyInstance;
            prop.updateValue((int)(double)expVal, world_time);
        } else if (propertyInstance.getPropertyDefinition().getType() == PropertyType.FLOAT) {
            PropertyInstance<Double> prop = (PropertyInstance<Double>) propertyInstance;
            prop.updateValue(expVal, world_time);
        }
    }
}
