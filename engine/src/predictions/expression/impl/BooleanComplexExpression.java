package predictions.expression.impl;

import dto.subdto.read.dto.rule.ExpressionErrorDto;
import dto.subdto.read.dto.rule.RuleErrorDto;
import predictions.action.api.ContextDefinition;
import predictions.action.impl.ContextDefinitionImpl;
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

    public BooleanComplexExpression(PRDCondition prdCondition,
                                    ContextDefinition contextDefinition,
                                    ExpressionErrorDto.Builder builder) {
        switch(prdCondition.getSingularity().toLowerCase())
        {
            case "multiple":
                res = new DualBooleanExpression(prdCondition, contextDefinition, builder);
                break;
            case "single":
                res = new SingleBooleanExpression(prdCondition, contextDefinition, builder);
                break;
        }
    }

    @Override
    public Comparable<Boolean> evaluate(Context context) {
        return res.evaluate(context);
    }

    @Override
    public String toString() {
        return res.toString();
    }
}
