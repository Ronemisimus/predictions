package predictions.expression.impl;

import predictions.execution.context.Context;
import predictions.expression.api.Expression;

public class NumberExpression implements Expression<Double> {

    private Double number;

    public NumberExpression(Double number) {
        this.number = number;
    }

    @Override
    public Comparable<Double> evaluate(Context context) {
        return number;
    }
}
