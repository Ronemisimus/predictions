package predictions.expression.impl;

import predictions.action.api.ContextDefinition;
import predictions.action.impl.ContextDefinitionImpl;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.environment.api.EnvVariablesManager;
import predictions.definition.property.api.PropertyDefinition;
import predictions.exception.BadExpressionException;
import predictions.exception.BadFunctionExpressionException;
import predictions.exception.BadPropertyTypeExpressionException;
import predictions.exception.MissingPropertyExpressionException;
import predictions.execution.context.Context;
import predictions.expression.ExpressionBuilder;
import predictions.expression.api.Expression;
import predictions.expression.api.SingleBooleanOperation;
import predictions.generated.PRDCondition;
import predictions.generated.PRDEntity;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class SingleBooleanExpression implements Expression<Boolean> {

    private final Expression<?> property;
    private final SingleBooleanOperation operation;
    private final Expression<?> valueExpression;

    public SingleBooleanExpression(PRDCondition prdCondition,
                                   ContextDefinition contextDefinition) throws BadExpressionException, MissingPropertyExpressionException, BadFunctionExpressionException, BadPropertyTypeExpressionException {
        operation = Arrays.stream(SingleBooleanOperation.values())
                .filter(op -> op.getVal().equals(prdCondition.getOperator().toLowerCase()))
                .findFirst().orElseThrow(() -> new RuntimeException("bad Single Expression. unknown operator " + prdCondition.getOperator()));
        property = ExpressionBuilder.buildGenericExpression(prdCondition.getProperty(), contextDefinition);
        valueExpression = ExpressionBuilder.buildGenericExpression(prdCondition.getValue(), contextDefinition);
    }

    @Override
    public Comparable<Boolean> evaluate(Context context) {
        Comparable<?> propVal = property.evaluate(context);
        Comparable<?> expVal = valueExpression.evaluate(context);

        Double b = null, a = null;

        if (!legalCombination(propVal,expVal))
        {
            throw new RuntimeException("bad Single Expression. cannot compare property " + property + " to expression " + valueExpression);
        }

        if (propVal instanceof Integer)
        {
             a = (double)(int)(Integer)propVal;
        }

        if (expVal instanceof Integer)
        {
            b = (double)(int)(Integer)expVal;
        }

        return operation.evaluate(a==null?propVal:a,b==null?expVal:b);
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

    @Override
    public String toString() {
        return "( " + property.toString() + " ) " + operation + " ( " + valueExpression + " )";
    }
}
