package predictions.expression.impl;

import predictions.definition.entity.EntityDefinition;
import predictions.execution.context.Context;
import predictions.expression.api.Expression;
import predictions.generated.PRDCondition;

public class StringComplexExpression implements Expression<String> {

    private String res;

    public StringComplexExpression(String expression, EntityDefinition ent) {
        res = expression;
    }

    @Override
    public Comparable<String> evaluate(Context context) {
        return res;
    }
}
