package predictions.exception;

import predictions.action.api.ContextDefinition;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.property.api.PropertyType;

public class MissingPropertyExpressionException extends Throwable {

    private final String finalExpression;
    private final ContextDefinition contextDefinition;
    private final boolean environment;
    private final PropertyType expectedType;
    public MissingPropertyExpressionException(String finalExpression, ContextDefinition contextDefinition, boolean environment, PropertyType expectedType) {
        this.finalExpression = finalExpression;
        this.environment = environment;
        this.contextDefinition = contextDefinition;
        this.expectedType = expectedType;
    }

    public String getFinalExpression() {
        return finalExpression;
    }

    public ContextDefinition getContextDefinition() {
        return contextDefinition;
    }

    public PropertyType getExpectedType() {
        return expectedType;
    }

    public boolean isEnvironment() {
        return environment;
    }
}
