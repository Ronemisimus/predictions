package predictions.expression.api;

public abstract class DualExpression<T> implements Expression<T> {

    private final Expression<T> expression1;
    private final Expression<T> expression2;

    public Expression<T> getExpression1() {
        return expression1;
    }

    public Expression<T> getExpression2() {
        return expression2;
    }

    public DualExpression(Expression<T> a, Expression<T> b) {
        this.expression1 = a;
        this.expression2 = b;
    }
}
