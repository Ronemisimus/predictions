package predictions.expression;

import predictions.execution.context.Context;
import predictions.execution.instance.property.PropertyInstance;
import predictions.expression.api.Expression;

public class BooleanEnviromentExpression implements Expression<Boolean> {
    private String property;
    public BooleanEnviromentExpression(String valueExpression) {
        this.property = valueExpression;
    }

    @Override
    public Comparable<Boolean> evaluate(Context context) {
        PropertyInstance<?> p = context.getEnvironmentVariable(property);
        switch (p.getPropertyDefinition().getType()) {
            case BOOLEAN:
                return (Boolean) p.getValue();
            default:
                throw new RuntimeException("environment variable must be a Boolean in expression");
        }
    }
}
