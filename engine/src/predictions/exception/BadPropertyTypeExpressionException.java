package predictions.exception;

import predictions.definition.property.api.PropertyType;

public class BadPropertyTypeExpressionException extends Throwable {

    private final String expression;
    private final PropertyType type;
    public BadPropertyTypeExpressionException(String expression, PropertyType type) {
        this.expression = expression;
        this.type = type;
    }

    public String getExpression() {
        return expression;
    }

    public PropertyType getType() {
        return type;
    }
}
