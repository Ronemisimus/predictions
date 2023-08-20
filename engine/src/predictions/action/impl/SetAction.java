package predictions.action.impl;

import predictions.action.api.AbstractAction;
import predictions.action.api.ActionType;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.environment.api.EnvVariablesManager;
import predictions.definition.property.api.PropertyDefinition;
import predictions.definition.property.api.PropertyType;
import predictions.exception.*;
import predictions.execution.context.Context;
import predictions.execution.instance.property.PropertyInstance;
import predictions.expression.ExpressionBuilder;
import predictions.expression.api.Expression;
import predictions.expression.impl.DoubleComplexExpression;

import java.util.Optional;

public class SetAction extends AbstractAction {

    private final String property;
    private Expression<?> valueExpression;

    public SetAction(EntityDefinition entityDefinition,
                     String property,
                     String valueExpression,
                     EnvVariablesManager env) throws BadExpressionException, MissingPropertyExpressionException, BadFunctionExpressionException, BadPropertyTypeExpressionException, MissingPropertyActionException {
        super(ActionType.SET, entityDefinition);
        this.property = property;
        Optional<PropertyDefinition<?>> prop = entityDefinition.getProps().stream()
                .filter(p -> p.getName().equals(property)).findFirst();
        if (!prop.isPresent()) throw new MissingPropertyActionException(property, ActionType.SET);
        switch (prop.get().getType())
        {
            case DECIMAL:
            case FLOAT:
                this.valueExpression = ExpressionBuilder.buildDoubleExpression(valueExpression, entityDefinition, env);
                break;
            case STRING:
                this.valueExpression = ExpressionBuilder.buildStringExpression(valueExpression, entityDefinition, env);
                break;
            case BOOLEAN:
                this.valueExpression = ExpressionBuilder.buildBooleanExpression(valueExpression, entityDefinition, env);
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
}
