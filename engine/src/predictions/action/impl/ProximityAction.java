package predictions.action.impl;

import dto.subdto.show.world.action.ActionDto;
import dto.subdto.show.world.action.ProximityActionDto;
import predictions.ConverterPRDEngine;
import predictions.action.api.AbstractAction;
import predictions.action.api.Action;
import predictions.action.api.ActionType;
import predictions.action.api.ContextDefinition;
import predictions.exception.*;
import predictions.execution.context.Context;
import predictions.expression.ExpressionBuilder;
import predictions.expression.api.Expression;
import predictions.generated.PRDActions;

import java.util.List;
import java.util.stream.Collectors;

public class ProximityAction extends AbstractAction {

    private final List<Action> actions;
    private final Expression<Double> distanceOf1;

    public ProximityAction(ContextDefinition contextDefinition, String s, PRDActions prdActions) throws BadExpressionException, MissingPropertyExpressionException, BadFunctionExpressionException, BadPropertyTypeExpressionException {
        super(ActionType.PROXIMITY, contextDefinition);
        actions = prdActions==null? null:
                prdActions.getPRDAction().stream()
                        .map(prdAction-> {
                            try {
                                return ConverterPRDEngine.getActionFromPRD(prdAction,contextDefinition);
                            } catch (BadExpressionException e) {
                                throw new RuntimeException(e);
                            } catch (MissingPropertyExpressionException e) {
                                throw new RuntimeException(e);
                            } catch (BadFunctionExpressionException e) {
                                throw new RuntimeException(e);
                            } catch (BadPropertyTypeExpressionException e) {
                                throw new RuntimeException(e);
                            } catch (MissingPropertyActionException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .collect(Collectors.toList());
        distanceOf1 = ExpressionBuilder.buildDoubleExpression(s, contextDefinition);
    }

    @Override
    public void invoke(Context context) {
        // TODO: complete proximity invoke
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
