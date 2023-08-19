package predictions.action.impl;

import dto.subdto.show.world.ActionDto;
import predictions.ConverterPRDEngine;
import predictions.action.api.AbstractAction;
import predictions.action.api.Action;
import predictions.action.api.ActionType;
import predictions.definition.entity.EntityDefinition;
import predictions.execution.context.Context;
import predictions.expression.api.Expression;
import predictions.expression.impl.BooleanComplexExpression;
import predictions.expression.impl.DualBooleanExpression;
import predictions.generated.PRDAction;
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

    public ConditionAction(EntityDefinition entityDefinition,
                           Expression<Boolean> condition,
                           Collection<Action> then_actions,
                           Collection<Action> else_actions) {
        super(ActionType.CONDITION, entityDefinition);
        this.condition = condition;
        this.then_actions = then_actions;
        this.else_actions = else_actions;
    }

    public ConditionAction(EntityDefinition ent, PRDCondition prdCondition, PRDThen prdThen, PRDElse prdElse) {
        super(ActionType.CONDITION, ent);
        this.condition = new BooleanComplexExpression(prdCondition, ent);
        this.then_actions = prdThen==null? new ArrayList<>(): prdThen.getPRDAction().stream()
                .map(def -> ConverterPRDEngine.getActionFromPRD(def, ent))
                .collect(Collectors.toList());
        this.else_actions = prdElse==null? new ArrayList<>(): prdElse.getPRDAction().stream()
                .map(def -> ConverterPRDEngine.getActionFromPRD(def, ent))
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
