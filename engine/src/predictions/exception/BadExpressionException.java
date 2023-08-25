package predictions.exception;

public class BadExpressionException extends Throwable {

    private final String expression;
    public BadExpressionException(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }
}
