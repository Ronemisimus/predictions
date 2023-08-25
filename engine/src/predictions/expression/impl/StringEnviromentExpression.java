package predictions.expression.impl;

import predictions.definition.property.api.PropertyType;
import predictions.execution.context.Context;
import predictions.execution.instance.property.PropertyInstance;
import predictions.expression.api.Expression;

import java.util.Objects;

public class StringEnviromentExpression implements Expression<String> {

    private final String property;
    public StringEnviromentExpression(String propName) {
        this.property = propName;
    }

    @Override
    public Comparable<String> evaluate(Context context) {
        PropertyInstance<?> p = context.getEnvironmentVariable(property);
        if (Objects.requireNonNull(p.getPropertyDefinition().getType()) == PropertyType.STRING) {
            return (String) p.getValue();
        }
        throw new RuntimeException("environment variable must be a String in expression");
    }
}
