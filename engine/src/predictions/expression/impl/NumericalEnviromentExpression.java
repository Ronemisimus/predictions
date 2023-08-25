package predictions.expression.impl;

import predictions.execution.context.Context;
import predictions.execution.instance.property.PropertyInstance;
import predictions.expression.api.Expression;

public class NumericalEnviromentExpression implements Expression<Double> {
    private final String property;

    public NumericalEnviromentExpression(String property) {
        this.property = property;
    }

    @Override
    public Comparable<Double> evaluate(Context context) {
        PropertyInstance<?> p = context.getEnvironmentVariable(property);
        switch (p.getPropertyDefinition().getType()) {
            case DECIMAL:
                return ((Integer)p.getValue()).doubleValue();
            case FLOAT:
                return (Double) p.getValue();
            default:
                throw new RuntimeException("environment variable must be a Number in expression");
        }
    }
}
