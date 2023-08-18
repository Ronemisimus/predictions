package predictions.expression.impl;

import predictions.action.api.AbstractAction;
import predictions.definition.property.api.PropertyDefinition;
import predictions.execution.context.Context;
import predictions.execution.instance.property.PropertyInstance;
import predictions.execution.instance.property.PropertyInstanceImpl;
import predictions.expression.api.Expression;
import predictions.expression.api.SingleBooleanOperation;

public class SingleBooleanExpression implements Expression<Boolean> {

    private String property;
    private SingleBooleanOperation operation;
    private Expression<?> valueExpression;

    public SingleBooleanExpression(String property, SingleBooleanOperation operation, Expression<?> valueExpression) {
        this.property = property;
        this.operation = operation;
        this.valueExpression = valueExpression;
    }

    @Override
    public Comparable<Boolean> evaluate(Context context) {
        Comparable<?> propVal = context.getPrimaryEntityInstance().getPropertyByName(property).getValue();
        Comparable<?> expVal = valueExpression.evaluate(context);

        if (!legalCombination(propVal,expVal))
        {
            throw new RuntimeException("bad Single Expression. cannot compare property " + property + " to expression " + valueExpression);
        }

        if (propVal instanceof Integer)
        {
             propVal = (double)(int)(Comparable<Integer>)propVal;
        }

        if (expVal instanceof Integer)
        {
            expVal = (double)(int)(Comparable<Integer>)expVal;
        }

        return operation.evaluate(propVal,expVal);
    }

    private static boolean legalCombination(Comparable<?> a, Comparable<?> b)
    {
        if (a instanceof String && b instanceof String)return true;
        if (a instanceof Boolean && b instanceof Boolean) return true;
        if (a instanceof Integer && b instanceof Integer) return true;
        if(a instanceof Double && b instanceof Double) return true;
        if (a instanceof Integer && b instanceof Double) return true;
        return a instanceof Double && b instanceof Integer;
    }
}
