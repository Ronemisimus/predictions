package predictions.expression.impl;

import predictions.execution.context.Context;
import predictions.expression.api.Expression;

import java.util.Random;

public class RandomExpression implements Expression<Double> {

    private Integer to;
    private Random random;

    public RandomExpression(Integer to) {
        this.to = to;
        this.random = new Random();
    }

    public static Expression<Double> BuildInstance(String simpleExpression) {
        int to = Integer.parseInt(simpleExpression.substring(simpleExpression.indexOf('('), simpleExpression.indexOf(')')));
        return new RandomExpression(to);
    }

    @Override
    public Double evaluate(Context context) {
        return (double) random.nextInt(to+1);
    }
}
