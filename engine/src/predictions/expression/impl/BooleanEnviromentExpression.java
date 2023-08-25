package predictions.expression.impl;

import predictions.definition.property.api.PropertyType;
import predictions.execution.context.Context;
import predictions.execution.instance.property.PropertyInstance;
import predictions.expression.api.Expression;

import java.util.Objects;

public class BooleanEnviromentExpression implements Expression<Boolean> {
    private final String property;
    public BooleanEnviromentExpression(String valueExpression) {
        this.property = valueExpression;
    }

    @Override
    public Comparable<Boolean> evaluate(Context context) {
        PropertyInstance<?> p = context.getEnvironmentVariable(property);
        if (Objects.requireNonNull(p.getPropertyDefinition().getType()) == PropertyType.BOOLEAN) {
            return (Boolean) p.getValue();
        }
        throw new RuntimeException("environment variable must be a Boolean in expression");
    }
}
