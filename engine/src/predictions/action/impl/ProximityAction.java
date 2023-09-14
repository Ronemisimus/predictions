package predictions.action.impl;

import dto.subdto.read.dto.rule.ActionErrorDto;
import dto.subdto.read.dto.rule.ExpressionErrorDto;
import dto.subdto.show.world.action.ActionDto;
import dto.subdto.show.world.action.ProximityActionDto;
import predictions.ConverterPRDEngine;
import predictions.action.api.AbstractAction;
import predictions.action.api.Action;
import predictions.action.api.ActionType;
import predictions.action.api.ContextDefinition;
import predictions.execution.context.Context;
import predictions.execution.instance.entity.EntityInstance;
import predictions.expression.ExpressionBuilder;
import predictions.expression.api.Expression;
import predictions.generated.PRDActions;

import java.util.List;
import java.util.stream.Collectors;

public class ProximityAction extends AbstractAction {

    private final List<Action> actions;
    private final Expression<Double> distanceOf1;

    public ProximityAction(ContextDefinition contextDefinition,
                           String s,
                           PRDActions prdActions,
                           ActionErrorDto.Builder builder) {
        super(ActionType.PROXIMITY, contextDefinition);
        actions = prdActions==null? null:
                prdActions.getPRDAction().stream()
                        .map(prdAction-> ConverterPRDEngine.getActionFromPRD(prdAction,contextDefinition, builder))
                        .collect(Collectors.toList());
        ExpressionErrorDto.Builder expressionBuilder = new ExpressionErrorDto.Builder();
        try {
            distanceOf1 = ExpressionBuilder.buildDoubleExpression(s, contextDefinition, expressionBuilder);
        }catch (Exception e)
        {
            builder.expressionError(expressionBuilder.build());
            throw e;
        }
    }

    @Override
    public void invoke(Context context) {
        EntityInstance primary = context.getPrimaryEntityInstance();
        EntityInstance secondary = context.getSecondaryEntityInstance();
        if (primary == null || secondary == null)
            return;
        Integer distMax = ((Double)distanceOf1.evaluate(context)).intValue();
        Integer distance = primary.getLocation().distance(secondary.getLocation());
        if (distance <= distMax)
            actions.forEach(a->a.invoke(context));
    }

    public List<Action> getActions() {
        return actions;
    }

    public Expression<Double> getDistanceOf1() {
        return distanceOf1;
    }

    @Override
    public ActionDto getDto() {
        return new ProximityActionDto(
                getContextDefinition().getPrimaryEntityDefinition().getDto(),
                getContextDefinition().getSecondaryEntityDefinition() == null? null:
                        getContextDefinition().getSecondaryEntityDefinition().getDto(),
                distanceOf1.toString(),
                actions.stream().map(Action::getDto).collect(Collectors.toList())
        );
    }
}
