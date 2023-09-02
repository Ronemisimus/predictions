package predictions.expression.impl;

import predictions.execution.context.Context;
import predictions.expression.api.Expression;

public class BasicBooleanExpression implements Expression<Boolean> {

    private final Comparable<Boolean> res;

    public BasicBooleanExpression(Boolean res) {
        this.res = res;
    }

    @Override
    public Comparable<Boolean> evaluate(Context context) {
        return res;
    }
}
