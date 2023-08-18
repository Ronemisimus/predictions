package predictions.expression.impl;

import predictions.execution.context.Context;
import predictions.expression.api.BooleanOperation;
import predictions.expression.api.DualExpression;
import predictions.expression.api.Expression;

public class DualBooleanExpression extends DualExpression<Boolean> {

    private BooleanOperation booleanOperation;

    public DualBooleanExpression(BooleanOperation booleanOperation, Expression<Boolean> a, Expression<Boolean> b) {
        super(a, b);
        this.booleanOperation = booleanOperation;
    }

    @Override
    public Comparable<Boolean> evaluate(Context context) {
        return booleanOperation.evaluate(
                getExpression1().evaluate(context),
                getExpression2().evaluate(context)
        );
    }
}
