package predictions.action.impl;

import dto.subdto.read.dto.rule.ActionErrorDto;
import dto.subdto.read.dto.rule.ExpressionErrorDto;
import dto.subdto.show.world.action.ActionDto;
import dto.subdto.show.world.action.ConditionActionDto;
import predictions.ConverterPRDEngine;
import predictions.action.api.AbstractAction;
import predictions.action.api.Action;
import predictions.action.api.ActionType;
import predictions.action.api.ContextDefinition;
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

    public ConditionAction(ContextDefinition contextDefinition,
                           PRDCondition prdCondition,
                           PRDThen prdThen,
                           PRDElse prdElse,
                           ActionErrorDto.Builder builder) {
        super(ActionType.CONDITION, contextDefinition);
        ExpressionErrorDto.Builder Builder = new ExpressionErrorDto.Builder();
        try {
            this.condition = new BooleanComplexExpression(prdCondition, contextDefinition, Builder);
        }catch (Exception e)
        {
            builder.expressionError(Builder.build());
            throw e;
        }
        this.then_actions = prdThen==null? new ArrayList<>(): prdThen.getPRDAction().stream()
                .map(def -> ConverterPRDEngine.getActionFromPRD(def, contextDefinition, builder))
                .collect(Collectors.toList());
        this.else_actions = prdElse==null? new ArrayList<>(): prdElse.getPRDAction().stream()
                .map(def -> ConverterPRDEngine.getActionFromPRD(def, contextDefinition, builder))
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

    @Override
    public ActionDto getDto() {
        return new ConditionActionDto(
                getContextDefinition().getPrimaryEntityDefinition().getDto(),
                getContextDefinition().getSecondaryEntityDefinition() == null ? null :
                        getContextDefinition().getSecondaryEntityDefinition().getDto(),
                condition.toString(),
                then_actions.stream().map(Action::getDto).collect(Collectors.toList()),
                else_actions.stream().map(Action::getDto).collect(Collectors.toList())
        );
    }
}
