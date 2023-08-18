package predictions.action.impl;

import predictions.action.api.AbstractAction;
import predictions.action.api.ActionType;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.property.api.PropertyType;
import predictions.execution.context.Context;
import predictions.execution.instance.property.PropertyInstance;
import predictions.expression.api.Expression;
import predictions.expression.api.MathOperation;
import predictions.expression.impl.DoubleComplexExpression;

public class DecreaseAction extends AbstractAction {

    private final String property;
    private final Expression<Double> byExpression;

    public DecreaseAction(EntityDefinition entityDefinition, String property, String byExpression) {
        super(ActionType.DECREASE, entityDefinition);
        this.property = property;
        this.byExpression = new DoubleComplexExpression(byExpression);
    }

    @Override
    public void invoke(Context context) {
        PropertyInstance<?> propertyInstance = context.getPrimaryEntityInstance().getPropertyByName(property);
        if (verifyNonNumericPropertyType(propertyInstance)) {
            throw new IllegalArgumentException("increase action can't operate on a none number property [" + property);
        }

        Comparable<Double> expVal = byExpression.evaluate(context);

        int world_time = context.getTick();

        if (propertyInstance.getPropertyDefinition().getType() == PropertyType.DECIMAL) {
            PropertyInstance<Integer> prop = (PropertyInstance<Integer>) propertyInstance;
            Comparable<Double> val = (double) (int) prop.getValue();
            Comparable<Double> res = MathOperation.SUBTRACT.evaluate(val, expVal);
            prop.updateValue((int)(double)res, world_time);
        } else if (propertyInstance.getPropertyDefinition().getType() == PropertyType.FLOAT) {
            PropertyInstance<Double> prop = (PropertyInstance<Double>) propertyInstance;
            Comparable<Double> val = prop.getValue();
            Comparable<Double> res = MathOperation.SUBTRACT.evaluate(val, expVal);
            prop.updateValue(res, world_time);
        }
    }
}
