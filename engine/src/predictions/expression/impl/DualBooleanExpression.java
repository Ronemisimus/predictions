package predictions.expression.impl;

import predictions.execution.context.Context;
import predictions.expression.api.BooleanOperation;
import predictions.expression.api.DualExpression;
import predictions.expression.api.Expression;

public class DualBooleanExpression extends DualExpression<Boolean> {

    private BooleanOperation booleanOperation;

    public DualBooleanExpression(BooleanOperation booleanOperation, Expression a, Expression b) {
        super(a, b);
        this.booleanOperation = booleanOperation;
    }

    @Override
    public Boolean evaluate(Context context) {
        return booleanOperation.evaluate(
                (Boolean) getExpression1().evaluate(context),
                (Boolean) getExpression2().evaluate(context)
        );
    }
}
