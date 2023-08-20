package predictions.expression.impl;

import predictions.definition.entity.EntityDefinition;
import predictions.definition.environment.api.EnvVariablesManager;
import predictions.exception.BadExpressionException;
import predictions.exception.BadFunctionExpressionException;
import predictions.exception.BadPropertyTypeExpressionException;
import predictions.exception.MissingPropertyExpressionException;
import predictions.execution.context.Context;
import predictions.expression.api.Expression;
import predictions.generated.PRDCondition;

public class BooleanComplexExpression implements Expression<Boolean> {

    private Expression<Boolean> res;

    public BooleanComplexExpression(PRDCondition prdCondition, EntityDefinition ent, EnvVariablesManager env) throws BadExpressionException, MissingPropertyExpressionException, BadFunctionExpressionException, BadPropertyTypeExpressionException {
        switch(prdCondition.getSingularity().toLowerCase())
        {
            case "multiple":
                res = new DualBooleanExpression(prdCondition, ent, env);
                break;
            case "single":
                res = new SingleBooleanExpression(prdCondition, ent, env);
                break;
        }
    }

    public BooleanComplexExpression(String value, EntityDefinition ent) {
        Boolean res;
        try {
            res = Boolean.getBoolean(value);
        }catch (Exception e){
            throw new RuntimeException("bad Boolean Expression. cannot compare boolean property to expression " + value);
        }
        this.res = context -> res;
    }

    @Override
    public Comparable<Boolean> evaluate(Context context) {
        return res.evaluate(context);
    }
}
