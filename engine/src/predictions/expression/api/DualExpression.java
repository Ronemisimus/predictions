package predictions.expression.api;

public abstract class DualExpression<T> implements Expression<T> {

    private Expression<T> expression1;
    private Expression<T> expression2;

    public Expression<T> getExpression1() {
        return expression1;
    }

    public Expression<T> getExpression2() {
        return expression2;
    }

    public DualExpression(Expression a, Expression b) {
        this.expression1 = a;
        this.expression2 = b;
    }
}
