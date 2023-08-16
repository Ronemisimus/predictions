package predictions.expression.impl;

import predictions.definition.entity.EntityDefinition;
import predictions.execution.context.Context;
import predictions.expression.api.Expression;
import predictions.expression.api.SingleBooleanOperation;

public class SingleBooleanExpression implements Expression<Boolean> {

    private String property;
    private SingleBooleanOperation operation;
    private Expression valueExpression;

    public SingleBooleanExpression(String property, SingleBooleanOperation operation, Expression valueExpression) {
        this.property = property;
        this.operation = operation;
        this.valueExpression = valueExpression;
    }

    @Override
    public Boolean evaluate(Context context) {
        Comparable val = (Comparable) context.getPrimaryEntityInstance().getPropertyByName(property).getValue();
        return operation.evaluate(val, (Comparable) valueExpression.evaluate(context));
    }
}
