package predictions.expression.impl;

import predictions.execution.context.Context;
import predictions.expression.api.Expression;

public class PropertyExpression<T> implements Expression<T> {
    private String property;

    public PropertyExpression(String property) {
        this.property = property;
    }

    public static Expression<Double> BuildDoubleInstance(String simpleExpression) {
        String property = simpleExpression.substring(simpleExpression.indexOf('.'), simpleExpression.indexOf(')'));
        return new PropertyExpression<Double>(property);
    }

    @Override
    public T evaluate(Context context) {
        return (T) context.getPrimaryEntityInstance().getPropertyByName(property).getValue();
    }
}
