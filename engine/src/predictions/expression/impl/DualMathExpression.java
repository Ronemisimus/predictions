package predictions.expression.impl;

import predictions.execution.context.Context;
import predictions.expression.api.DualExpression;
import predictions.expression.api.Expression;
import predictions.expression.api.MathOperation;

public class DualMathExpression extends DualExpression<Double> {

    private MathOperation mathOperation;

    public DualMathExpression(MathOperation mathOperation, Expression<Double> a, Expression<Double> b) {
        super(a, b);
        this.mathOperation = mathOperation;
    }

    @Override
    public Comparable<Double> evaluate(Context context) {
        return mathOperation.evaluate(
                getExpression1().evaluate(context),
                getExpression2().evaluate(context)
        );
    }
}
