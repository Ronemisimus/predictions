package predictions.expression.impl;

import predictions.execution.context.Context;
import predictions.expression.api.Expression;

public class TicksExpression implements Expression<Double> {

    private String property;

    public TicksExpression(String property) {
        this.property = property;
    }

    public static Expression<Double> BuildInstance(String simpleExpression) {
        String property = simpleExpression.substring(simpleExpression.indexOf('.') + 1);
        return new TicksExpression(property);
    }

    @Override
    public Double evaluate(Context context) {
        return (double) context.getPrimaryEntityInstance().getPropertyByName(property).getTimeModification();
    }
}
