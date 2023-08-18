package predictions.expression.impl;

import predictions.execution.context.Context;
import predictions.expression.api.Expression;

public class PropertyExpression<T> implements Expression<T> {
    private final String property;

    public PropertyExpression(String property) {
        this.property = property;
    }

    public static Expression<Double> BuildDoubleInstance(String simpleExpression) {
        String property = simpleExpression.substring(simpleExpression.indexOf('.')+1, simpleExpression.indexOf(')'));
        return new PropertyExpression<>(property);
    }

    @Override
    public Comparable<T> evaluate(Context context) {
        return (Comparable<T>) context.getPrimaryEntityInstance().getPropertyByName(property).getValue();
    }
}
