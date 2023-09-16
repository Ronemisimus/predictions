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
        PropertyInstance<?> res = context.getEnvironmentVariable(property);
        if (res == null) throw new RuntimeException("environment variable \"" + property + "\" doesn't exist");
        //noinspection unchecked
        return (Comparable<T>) res.getValue();
    }

    @Override
    public String toString() {
        return "environment(" + property + ")";
    }
}
