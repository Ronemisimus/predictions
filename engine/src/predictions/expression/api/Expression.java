package predictions.expression.api;

import predictions.exception.BadPropertyTypeExpressionException;
import predictions.execution.context.Context;

public interface Expression<T> {
    Comparable<T> evaluate(Context context);
}
