package predictions.expression.impl;

import predictions.execution.context.Context;
import predictions.expression.api.Expression;

public class IntegerWrapExpression implements Expression<Double> {
    private final Expression<Integer> inner;

    public IntegerWrapExpression(Expression<Integer> inner) {
        this.inner = inner;
    }

    @Override
    public Comparable<Double> evaluate(Context context) {
        return ((Integer)inner.evaluate(context)).doubleValue();
    }

    @Override
    public String toString() {
        return inner.toString();
    }
}
