package predictions.expression.impl;

import predictions.definition.entity.EntityDefinition;
import predictions.execution.context.Context;
import predictions.expression.api.Expression;
import predictions.generated.PRDCondition;

public class BooleanComplexExpression implements Expression<Boolean> {

    private Expression<Boolean> res;

    public BooleanComplexExpression(PRDCondition prdCondition, EntityDefinition ent) {
        switch(prdCondition.getSingularity().toLowerCase())
        {
            case "multiple":
                res = new DualBooleanExpression(prdCondition, ent);
                break;
            case "single":
                res = new SingleBooleanExpression(prdCondition, ent);
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
