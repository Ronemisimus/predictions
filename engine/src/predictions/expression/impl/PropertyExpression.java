package predictions.expression.impl;

import predictions.execution.context.Context;
import predictions.expression.api.Expression;

public class PropertyExpression<T> implements Expression<T> {
    private final String property;

    public PropertyExpression(String property) {
        this.property = property;
    }

    @Override
    public Comparable<T> evaluate(Context context) {
        return (Comparable<T>) context.getPrimaryEntityInstance().getPropertyByName(property).getValue();
    }
}
