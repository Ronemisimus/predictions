package predictions.expression.impl;

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

public class SingleBooleanExpression implements Expression<Boolean> {

    private final String property;
    private final SingleBooleanOperation operation;
    private final Expression<?> valueExpression;

    public SingleBooleanExpression(PRDCondition prdCondition, EntityDefinition ent, EnvVariablesManager env) throws BadExpressionException, MissingPropertyExpressionException, BadFunctionExpressionException, BadPropertyTypeExpressionException {
        this.property = prdCondition.getProperty();
        this.operation = SingleBooleanOperation.getInstance(prdCondition.getOperator().toLowerCase());
        PropertyDefinition<?> prop = ent.getProps().stream().filter(p -> p.getName().equals(this.property)).findAny().get();
        switch (prop.getType())
        {
            case BOOLEAN:
                this.valueExpression = ExpressionBuilder.buildBooleanExpression(prdCondition.getValue(), ent, env);
                break;
            case DECIMAL:
            case FLOAT:
                this.valueExpression = ExpressionBuilder.buildDoubleExpression(prdCondition.getValue(), ent, env);
                break;
            case STRING:
                this.valueExpression = ExpressionBuilder.buildStringExpression(prdCondition.getValue(), ent, env);
                break;
            default:
                throw new RuntimeException("bad Single Expression. cannot compare property " + property + " to expression " + prdCondition.getValue());
        }
    }

    @Override
    public Comparable<Boolean> evaluate(Context context) {
        Comparable<?> propVal = context.getPrimaryEntityInstance().getPropertyByName(property).getValue();
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
}
