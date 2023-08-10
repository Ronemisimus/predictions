package predictions.expression.impl;

import predictions.execution.context.Context;
import predictions.expression.api.DualExpression;
import predictions.expression.api.Expression;
import predictions.expression.api.MathOperation;

public class DualMathExpression extends DualExpression<Double> {

    private MathOperation mathOperation;

    public DualMathExpression(MathOperation mathOperation, Expression a, Expression b) {
        super(a, b);
        this.mathOperation = mathOperation;
    }

    @Override
    public Double evaluate(Context context) {
        return mathOperation.evaluate(
                (Double) getExpression1().evaluate(context),
                (Double) getExpression2().evaluate(context)
        );
    }
}
