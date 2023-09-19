package predictions.expression.impl;

import predictions.execution.context.Context;
import predictions.expression.api.Expression;

public class DoubleWrapExpression implements Expression<Integer> {
    private final Expression<Double> innerExpression;
    public DoubleWrapExpression(Expression<Double> doubleExpression) {
        this.innerExpression = doubleExpression;
    }

    @Override
    public Comparable<Integer> evaluate(Context context) {
        return ((Double)innerExpression.evaluate(context)).intValue();
    }

    @Override
    public String toString() {
        return innerExpression.toString();
    }
}
