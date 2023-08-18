package predictions.action.impl;

import predictions.action.api.AbstractAction;
import predictions.action.api.Action;
import predictions.action.api.ActionType;
import predictions.definition.entity.EntityDefinition;
import predictions.execution.context.Context;
import predictions.expression.api.Expression;

import java.util.Collection;

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

    @Override
    public void invoke(Context context) {
        if (condition.evaluate(context).equals(true)) {
            then_actions.forEach(a -> a.invoke(context));
        } else {
            else_actions.forEach(a -> a.invoke(context));
        }
    }
}
