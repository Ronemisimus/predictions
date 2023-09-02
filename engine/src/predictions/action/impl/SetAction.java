package predictions.action.impl;

import dto.subdto.show.world.action.ActionDto;
import dto.subdto.show.world.action.SetActionDto;
import predictions.action.api.AbstractAction;
import predictions.action.api.ActionType;
import predictions.action.api.ContextDefinition;
import predictions.definition.property.api.PropertyDefinition;
import predictions.definition.property.api.PropertyType;
import predictions.exception.*;
import predictions.execution.context.Context;
import predictions.execution.instance.property.PropertyInstance;
import predictions.expression.ExpressionBuilder;
import predictions.expression.api.Expression;

import java.util.Optional;

public class SetAction extends AbstractAction {

    private final String property;
    private Expression<?> valueExpression;

    public SetAction(ContextDefinition contextDefinition,
                     String property,
                     String valueExpression) throws BadExpressionException, MissingPropertyExpressionException, BadFunctionExpressionException, BadPropertyTypeExpressionException, MissingPropertyActionException {
        super(ActionType.SET, contextDefinition);
        this.property = property;
        Optional<PropertyDefinition<?>> prop = contextDefinition.getPrimaryEntityDefinition().getProps().stream()
                .filter(p -> p.getName().equals(property)).findFirst();
        if (!prop.isPresent()) throw new MissingPropertyActionException(property, ActionType.SET);
        switch (prop.get().getType())
        {
            case DECIMAL:
            case FLOAT:
                this.valueExpression = ExpressionBuilder.buildDoubleExpression(valueExpression, contextDefinition);
                break;
            case STRING:
                this.valueExpression = ExpressionBuilder.buildStringExpression(valueExpression, contextDefinition);
                break;
            case BOOLEAN:
                this.valueExpression = ExpressionBuilder.buildBooleanExpression(valueExpression, contextDefinition);
                break;
        }
    }
    @Override
    public void invoke(Context context) {
        PropertyInstance<?> propertyInstance = context.getPrimaryEntityInstance().getPropertyByName(property);
        if (verifyNonNumericPropertyType(propertyInstance)) {
            throw new IllegalArgumentException("increase action can't operate on a none number property [" + property);
        }

        Comparable<?> expVal = valueExpression.evaluate(context);

        int world_time = context.getTick();

        if (propertyInstance.getPropertyDefinition().getType() == PropertyType.DECIMAL) {
            PropertyInstance<Integer> prop = (PropertyInstance<Integer>) propertyInstance;
            prop.updateValue(expVal, world_time);
        } else if (propertyInstance.getPropertyDefinition().getType() == PropertyType.FLOAT) {
            PropertyInstance<Double> prop = (PropertyInstance<Double>) propertyInstance;
            prop.updateValue(expVal, world_time);
        }
        else if (propertyInstance.getPropertyDefinition().getType() == PropertyType.STRING) {
            PropertyInstance<String> prop = (PropertyInstance<String>) propertyInstance;
            prop.updateValue(expVal, world_time);
        }
        else if (propertyInstance.getPropertyDefinition().getType() == PropertyType.BOOLEAN) {
            PropertyInstance<Boolean> prop = (PropertyInstance<Boolean>) propertyInstance;
            prop.updateValue(expVal, world_time);
        }
    }

    @Override
    public ActionDto getDto() {
        return new SetActionDto(
                getContextDefinition().getPrimaryEntityDefinition().getDto(),
                getContextDefinition().getSecondaryEntityDefinition() == null ? null :
                        getContextDefinition().getSecondaryEntityDefinition().getDto(),
                property,
                valueExpression.toString()
        );
    }
}
