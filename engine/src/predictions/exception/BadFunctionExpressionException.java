package predictions.exception;

public class BadFunctionExpressionException extends Throwable {

    private final String expression;
    public BadFunctionExpressionException(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }
}
