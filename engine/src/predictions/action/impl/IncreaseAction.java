package predictions.action.impl;

import predictions.action.api.AbstractAction;
import predictions.action.api.ActionType;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.property.api.PropertyType;
import predictions.execution.context.Context;
import predictions.execution.instance.property.PropertyInstance;
import predictions.expression.api.Expression;
import predictions.expression.impl.DoubleComplexExpression;

public class IncreaseAction extends AbstractAction {

    private final String property;
    private final Expression<Double> byExpression;

    public IncreaseAction(EntityDefinition entityDefinition, String property, String byExpression) {
        super(ActionType.INCREASE, entityDefinition);
        this.property = property;
        this.byExpression = new DoubleComplexExpression(byExpression);
    }

    @Override
    public void invoke(Context context) {
        PropertyInstance propertyInstance = context.getPrimaryEntityInstance().getPropertyByName(property);
        if (!verifyNumericPropertyType(propertyInstance)) {
            throw new IllegalArgumentException("increase action can't operate on a none number property [" + property);
        }

        Double expVal = byExpression.evaluate(context);

        // TODO: get world ticks
        int world_time = 500;

        if (propertyInstance.getPropertyDefinition().getType() == PropertyType.DECIMAL) {
            int propVal = (Integer) propertyInstance.getValue();
            propertyInstance.updateValue(Math.round(propVal + expVal.doubleValue()), world_time);
        } else if (propertyInstance.getPropertyDefinition().getType() == PropertyType.FLOAT) {
            double propVal = (Double) propertyInstance.getValue();
            propertyInstance.updateValue(propVal + expVal.doubleValue(), world_time);
        }
    }
}
