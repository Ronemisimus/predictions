package predictions.action.impl;

import predictions.action.api.AbstractAction;
import predictions.action.api.Action;
import predictions.action.api.ActionType;
import predictions.definition.entity.EntityDefinition;
import predictions.execution.context.Context;
import predictions.expression.api.Expression;

import java.util.List;

public class ConditionAction extends AbstractAction {

    private Expression<Boolean> condition;

    private List<Action> then_actions;

    private List<Action> else_actions;

    public ConditionAction(EntityDefinition entityDefinition,
                           Expression<Boolean> condition,
                           List<Action> then_actions,
                           List<Action> else_actions) {
        super(ActionType.CONDITION, entityDefinition);
        this.condition = condition;
        this.then_actions = then_actions;
        this.else_actions = else_actions;
    }

    @Override
    public void invoke(Context context) {
        if (condition.evaluate(context)) {
            then_actions.forEach(a -> a.invoke(context));
        } else {
            else_actions.forEach(a -> a.invoke(context));
        }
    }
}
