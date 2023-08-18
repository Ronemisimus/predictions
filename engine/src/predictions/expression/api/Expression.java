package predictions.expression.api;

import predictions.execution.context.Context;

public interface Expression<T> {
    Comparable<T> evaluate(Context context);
}
