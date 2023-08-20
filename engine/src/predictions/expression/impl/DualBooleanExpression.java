package predictions.expression.impl;

import predictions.definition.entity.EntityDefinition;
import predictions.definition.environment.api.EnvVariablesManager;
import predictions.exception.BadExpressionException;
import predictions.exception.BadFunctionExpressionException;
import predictions.exception.BadPropertyTypeExpressionException;
import predictions.exception.MissingPropertyExpressionException;
import predictions.execution.context.Context;
import predictions.expression.api.BooleanOperation;
import predictions.expression.api.DualExpression;
import predictions.expression.api.Expression;
import predictions.generated.PRDCondition;

public class DualBooleanExpression extends DualExpression<Boolean> {

    private BooleanOperation booleanOperation;

    public DualBooleanExpression(PRDCondition prdCondition, EntityDefinition ent, EnvVariablesManager env) throws BadExpressionException, MissingPropertyExpressionException, BadFunctionExpressionException, BadPropertyTypeExpressionException {
        super(
                new BooleanComplexExpression(prdCondition.getPRDCondition().get(0), ent, env),
                new BooleanComplexExpression(subCondition(prdCondition), ent, env));
        this.booleanOperation = BooleanOperation.valueOf(prdCondition.getLogical().toUpperCase());
    }

    private static PRDCondition subCondition(PRDCondition prdCondition) {
        PRDCondition res = new PRDCondition();
        prdCondition.getPRDCondition().remove(0);
        int len = prdCondition.getPRDCondition().size();
        if(len>1)
        {
            res.getPRDCondition().addAll(prdCondition.getPRDCondition());
            res.setLogical(prdCondition.getLogical());
            res.setSingularity(prdCondition.getSingularity());
            return res;
        }
        else if (len==1)
        {
            return prdCondition.getPRDCondition().get(0);
        }
        else
        {
            return null;
        }
    }

    @Override
    public Comparable<Boolean> evaluate(Context context) {
        return booleanOperation.evaluate(
                getExpression1().evaluate(context),
                getExpression2().evaluate(context)
        );
    }
}
