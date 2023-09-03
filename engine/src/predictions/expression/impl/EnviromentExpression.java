package predictions.expression.impl;

import predictions.execution.context.Context;
import predictions.execution.instance.property.PropertyInstance;
import predictions.expression.api.Expression;

public class EnviromentExpression<T> implements Expression<T> {
    private final String property;

    public EnviromentExpression(String property) {
        this.property = property;
    }

    @Override
    public Comparable<T> evaluate(Context context) {
        return (Comparable<T>) context.getEnvironmentVariable(property);
    }

    @Override
    public String toString() {
        return "environment(" + property + ")";
    }
}
