package predictions.expression.impl;

import dto.subdto.read.dto.rule.ExpressionErrorDto;
import predictions.action.api.ContextDefinition;
import predictions.execution.context.Context;
import predictions.expression.api.BooleanOperation;
import predictions.expression.api.DualExpression;
import predictions.generated.PRDCondition;

import java.util.Objects;

public class DualBooleanExpression extends DualExpression<Boolean> {

    private final BooleanOperation booleanOperation;

    public DualBooleanExpression(PRDCondition prdCondition,
                                 ContextDefinition contextDefinition,
                                 ExpressionErrorDto.Builder builder) {
        super(
                new BooleanComplexExpression(prdCondition.getPRDCondition().get(0), contextDefinition, builder),
                new BooleanComplexExpression(Objects.requireNonNull(subCondition(prdCondition)), contextDefinition, builder));
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

    @Override
    public String toString() {
        return "( " + getExpression1().toString() + " )" + booleanOperation + " ( " + getExpression2().toString() + " )";
    }
}
