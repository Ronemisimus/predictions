package predictions.action.impl;

import dto.subdto.read.dto.rule.ActionErrorDto;
import dto.subdto.read.dto.rule.ExpressionErrorDto;
import dto.subdto.show.world.action.ActionDto;
import dto.subdto.show.world.action.SetActionDto;
import predictions.ConverterPRDEngine;
import predictions.action.api.AbstractAction;
import predictions.action.api.ActionType;
import predictions.action.api.ContextDefinition;
import predictions.definition.property.api.PropertyDefinition;
import predictions.definition.property.api.PropertyType;
import predictions.execution.context.Context;
import predictions.execution.instance.property.PropertyInstance;
import predictions.expression.ExpressionBuilder;
import predictions.expression.api.Expression;

import java.util.Optional;

public class SetAction extends AbstractAction {

    private final Boolean propInSecondary;
    private final String property;
    private Expression<?> valueExpression;

    public SetAction(ContextDefinition contextDefinition,
                     String entityName,
                     String property,
                     String valueExpression,
                     ActionErrorDto.Builder builder) {
        super(ActionType.SET, contextDefinition);
        this.property = property;
        this.propInSecondary = contextDefinition.getSecondaryEntityDefinition() !=null &&
                contextDefinition.getSecondaryEntityDefinition().getName().equals(entityName);

        ExpressionErrorDto.Builder expressionBuilder = new ExpressionErrorDto.Builder();

        Optional<PropertyDefinition<?>> prop = ConverterPRDEngine.checkEntityAndPropertyInContext(entityName, property, contextDefinition, builder);
        try{
            if (prop.isPresent())
            {
                switch (prop.get().getType())
                {
                    case DECIMAL:
                    case FLOAT:
                        this.valueExpression = ExpressionBuilder.buildDoubleExpression(valueExpression, contextDefinition, expressionBuilder);
                        break;
                    case STRING:
                        this.valueExpression = ExpressionBuilder.buildStringExpression(valueExpression, contextDefinition, expressionBuilder);
                        break;
                    case BOOLEAN:
                        this.valueExpression = ExpressionBuilder.buildBooleanExpression(valueExpression, contextDefinition, expressionBuilder);
                        break;
                }
            }
        }catch (Exception e)
        {
            builder.expressionError(expressionBuilder.build());
            throw e;
        }
    }
    @SuppressWarnings("unchecked")
    @Override
    public void invoke(Context context) {
        PropertyInstance<?> propertyInstance = propInSecondary? context.getSecondaryEntityInstance().getPropertyByName(property):
                context.getPrimaryEntityInstance().getPropertyByName(property);
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
