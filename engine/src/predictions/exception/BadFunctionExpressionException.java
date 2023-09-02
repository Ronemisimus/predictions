package predictions.exception;

import predictions.action.api.ContextDefinition;
import predictions.definition.property.api.PropertyType;

public class BadFunctionExpressionException extends Throwable {

    private final String expression;
    private final ContextDefinition contextDefinition;
    private final PropertyType typeExpected;
    public BadFunctionExpressionException(String expression, ContextDefinition contextDefinition, PropertyType typeExpected) {
        this.expression = expression;
        this.contextDefinition = contextDefinition;
        this.typeExpected = typeExpected;
    }

    public String getExpression() {
        return expression;
    }

    public PropertyType getTypeExpected() {
        return typeExpected;
    }

    public ContextDefinition getContextDefinition() {
        return contextDefinition;
    }
}
