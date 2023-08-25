package predictions.action.impl;

import predictions.ConverterPRDEngine;
import predictions.action.api.AbstractAction;
import predictions.action.api.Action;
import predictions.action.api.ActionType;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.environment.api.EnvVariablesManager;
import predictions.exception.*;
import predictions.execution.context.Context;
import predictions.expression.api.Expression;
import predictions.expression.impl.BooleanComplexExpression;
import predictions.generated.PRDCondition;
import predictions.generated.PRDElse;
import predictions.generated.PRDThen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class ConditionAction extends AbstractAction {

    private final Expression<Boolean> condition;

    private final Collection<Action> then_actions;

    private final Collection<Action> else_actions;

    public ConditionAction(EntityDefinition ent, PRDCondition prdCondition, PRDThen prdThen, PRDElse prdElse, EnvVariablesManager env) throws RuntimeException, BadExpressionException, MissingPropertyExpressionException, BadFunctionExpressionException, BadPropertyTypeExpressionException {
        super(ActionType.CONDITION, ent);
        this.condition = new BooleanComplexExpression(prdCondition, ent, env);
        this.then_actions = prdThen==null? new ArrayList<>(): prdThen.getPRDAction().stream()
                .map(def -> {
                    try {
                        return ConverterPRDEngine.getActionFromPRD(def, ent, env);
                    } catch (BadExpressionException | MissingPropertyActionException |
                             MissingPropertyExpressionException | BadFunctionExpressionException |
                             BadPropertyTypeExpressionException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        this.else_actions = prdElse==null? new ArrayList<>(): prdElse.getPRDAction().stream()
                .map(def -> {
                    try {
                        return ConverterPRDEngine.getActionFromPRD(def, ent, env);
                    } catch (BadExpressionException | MissingPropertyExpressionException |
                             BadFunctionExpressionException | BadPropertyTypeExpressionException |
                             MissingPropertyActionException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public void invoke(Context context) {
        if (condition.evaluate(context).equals(true)) {
            then_actions.forEach(a -> a.invoke(context));
        } else {
            else_actions.forEach(a -> a.invoke(context));
        }
    }
}
