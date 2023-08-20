package predictions.expression.impl;

import predictions.execution.context.Context;
import predictions.execution.instance.property.PropertyInstance;
import predictions.expression.api.Expression;

public class StringEnviromentExpression implements Expression<String> {

    private String property;
    public StringEnviromentExpression(String propName) {
        this.property = propName;
    }

    @Override
    public Comparable<String> evaluate(Context context) {
        PropertyInstance<?> p = context.getEnvironmentVariable(property);
        switch (p.getPropertyDefinition().getType()) {
            case STRING:
                return (String) p.getValue();
            default:
                throw new RuntimeException("environment variable must be a String in expression");
        }
    }
}
