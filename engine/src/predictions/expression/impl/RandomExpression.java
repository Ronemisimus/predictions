package predictions.expression.impl;

import predictions.execution.context.Context;
import predictions.expression.api.Expression;

import java.util.Random;

public class RandomExpression implements Expression<Double> {

    private final Integer to;
    private final Random random;

    public RandomExpression(Integer to) {
        this.to = to;
        this.random = new Random();
    }

    @Override
    public Comparable<Double> evaluate(Context context) {
        return (double) random.nextInt(to+1);
    }
}
