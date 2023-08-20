package predictions.exception;

public class BadFunctionExpressionException extends Throwable {

    private String expression;
    public BadFunctionExpressionException(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }
}
