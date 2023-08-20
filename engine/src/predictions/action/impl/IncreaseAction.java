package predictions.action.impl;

import predictions.action.api.AbstractAction;
import predictions.action.api.ActionType;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.environment.api.EnvVariablesManager;
import predictions.definition.property.api.PropertyType;
import predictions.exception.BadExpressionException;
import predictions.exception.BadFunctionExpressionException;
import predictions.exception.BadPropertyTypeExpressionException;
import predictions.exception.MissingPropertyExpressionException;
import predictions.execution.context.Context;
import predictions.execution.instance.property.PropertyInstance;
import predictions.expression.ExpressionBuilder;
import predictions.expression.api.Expression;
import predictions.expression.api.MathOperation;
import predictions.expression.impl.DoubleComplexExpression;

public class IncreaseAction extends AbstractAction {

    private final String property;
    private final Expression<Double> byExpression;

    public IncreaseAction(EntityDefinition entityDefinition, String property, String byExpression,
                          EnvVariablesManager env) throws BadExpressionException, MissingPropertyExpressionException, BadFunctionExpressionException, BadPropertyTypeExpressionException {
        super(ActionType.INCREASE, entityDefinition);
        this.property = property;
        this.byExpression = ExpressionBuilder.buildDoubleExpression(byExpression, entityDefinition, env);
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
            Comparable<Double> val = Double.valueOf((Integer) prop.getValue());
            Comparable<Double> res = MathOperation.ADD.evaluate(val, expVal);
            prop.updateValue(Integer.valueOf(((Double)res).intValue()), world_time);
        } else if (propertyInstance.getPropertyDefinition().getType() == PropertyType.FLOAT) {
            PropertyInstance<Double> prop = (PropertyInstance<Double>) propertyInstance;
            Comparable<Double> val = prop.getValue();
            Comparable<Double> res = MathOperation.ADD.evaluate(val, expVal);
            prop.updateValue(res, world_time);
        }
    }
}
