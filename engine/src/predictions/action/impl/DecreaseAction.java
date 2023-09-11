package predictions.action.impl;

import dto.subdto.read.dto.rule.ActionErrorDto;
import dto.subdto.read.dto.rule.ExpressionErrorDto;
import dto.subdto.show.world.action.ActionDto;
import dto.subdto.show.world.action.decreaseActionDto;
import predictions.ConverterPRDEngine;
import predictions.action.api.AbstractAction;
import predictions.action.api.ActionType;
import predictions.action.api.ContextDefinition;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.environment.api.EnvVariablesManager;
import predictions.definition.property.api.PropertyDefinition;
import predictions.definition.property.api.PropertyType;
import predictions.exception.BadExpressionException;
import predictions.exception.BadFunctionExpressionException;
import predictions.exception.BadPropertyTypeExpressionException;
import predictions.exception.MissingPropertyExpressionException;
import predictions.execution.context.Context;
import predictions.execution.instance.property.PropertyInstance;
import predictions.expression.ExpressionBuilder;
import predictions.expression.api.Expression;
import predictions.expression.api.MathOperation;

import java.util.Optional;

public class DecreaseAction extends AbstractAction {

    private final Boolean propInSecondary;
    private final String property;
    private final Expression<Double> byExpression;

    public DecreaseAction(ContextDefinition contextDefinition,
                          String entityName,
                          String property,
                          String byExpression,
                          ActionErrorDto.Builder builder) {
        super(ActionType.DECREASE, contextDefinition);
        ExpressionErrorDto.Builder expressionBuilder = new ExpressionErrorDto.Builder();
        this.property = property;
        this.propInSecondary = contextDefinition.getSecondaryEntityDefinition()!=null &&
                contextDefinition.getSecondaryEntityDefinition().getName().equals(entityName);

        Optional<PropertyDefinition<?>> propertyDefinition = ConverterPRDEngine.checkEntityAndPropertyInContext(entityName, property, contextDefinition, builder);

        if (propertyDefinition.isPresent() && verifyNonNumericPropertyType(propertyDefinition.get())) {
            builder.propertyTypeMismatch(property, ActionType.DECREASE.name());
            throw new IllegalArgumentException("increase action can't operate on a none number property [" + property);
        }

        try {
            this.byExpression = ExpressionBuilder.buildDoubleExpression(byExpression, contextDefinition, expressionBuilder);
        }catch (Exception e){
            builder.expressionError(expressionBuilder.build());
            throw e;
        }
    }

    @Override
    public void invoke(Context context) {
        PropertyInstance<?> propertyInstance =  propInSecondary? context.getSecondaryEntityInstance().getPropertyByName(property) :
                context.getPrimaryEntityInstance().getPropertyByName(property);

        Comparable<Double> expVal = byExpression.evaluate(context);

        int world_time = context.getTick();

        if (propertyInstance.getPropertyDefinition().getType() == PropertyType.DECIMAL) {
            PropertyInstance<Integer> prop = (PropertyInstance<Integer>) propertyInstance;
            Comparable<Double> val = (double) (int) prop.getValue();
            Comparable<Double> res = MathOperation.SUBTRACT.evaluate(val, expVal);
            prop.updateValue((int)(double)res, world_time);
        } else if (propertyInstance.getPropertyDefinition().getType() == PropertyType.FLOAT) {
            PropertyInstance<Double> prop = (PropertyInstance<Double>) propertyInstance;
            Comparable<Double> val = prop.getValue();
            Comparable<Double> res = MathOperation.SUBTRACT.evaluate(val, expVal);
            prop.updateValue(res, world_time);
        }
    }

    @Override
    public ActionDto getDto() {
        return new decreaseActionDto(
                property,
                byExpression.toString(),
                getContextDefinition().getPrimaryEntityDefinition().getDto(),
                getContextDefinition().getSecondaryEntityDefinition()==null?null:
                        getContextDefinition().getSecondaryEntityDefinition().getDto()
        );
    }
}
