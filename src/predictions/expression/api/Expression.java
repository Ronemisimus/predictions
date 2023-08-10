package predictions.expression.api;

import predictions.execution.context.Context;

public interface Expression<T> {
    T evaluate(Context context);
}
