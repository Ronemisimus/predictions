package predictions.action.impl;

import dto.subdto.read.dto.rule.ActionErrorDto;
import dto.subdto.read.dto.rule.ExpressionErrorDto;
import dto.subdto.show.world.action.ActionDto;
import dto.subdto.show.world.action.IncreaseActionDto;
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
import predictions.expression.api.MathOperation;

import java.util.Optional;

public class IncreaseAction extends AbstractAction {

    private final Boolean propInSecondary;
    private final String property;
    private final Expression<Double> byExpression;

    public IncreaseAction(ContextDefinition contextDefinition,
                          String entityName,
                          String property, String byExpression,
                          ActionErrorDto.Builder builder) {
        super(ActionType.INCREASE, contextDefinition);
        ExpressionErrorDto.Builder expBuilder = new ExpressionErrorDto.Builder();
        this.property = property;
        this.propInSecondary = contextDefinition.getSecondaryEntityDefinition()!=null &&
                contextDefinition.getSecondaryEntityDefinition().getName().equals(entityName);

        Optional<PropertyDefinition<?>> propertyDefinition = ConverterPRDEngine.checkEntityAndPropertyInContext(entityName, property, contextDefinition, builder);

        if (propertyDefinition.isPresent() && verifyNonNumericPropertyType(propertyDefinition.get())) {
            builder.propertyTypeMismatch(property, ActionType.INCREASE.name());
            throw new RuntimeException("increase action can't operate on a none number property " + property);
        }

        try {
            this.byExpression = ExpressionBuilder.buildDoubleExpression(byExpression, contextDefinition, expBuilder);
        }catch (Exception e) {
            builder.expressionError(expBuilder.build());
            throw e;
        }
    }

    @Override
    public void invoke(Context context) {
        PropertyInstance<?> propertyInstance = propInSecondary? context.getSecondaryEntityInstance().getPropertyByName(property):
                context.getPrimaryEntityInstance().getPropertyByName(property);

        Comparable<Double> expVal = byExpression.evaluate(context);

        int world_time = context.getTick();

        if (propertyInstance.getPropertyDefinition().getType() == PropertyType.DECIMAL) {
            //noinspection unchecked
            PropertyInstance<Integer> prop = (PropertyInstance<Integer>) propertyInstance;
            Comparable<Double> val = Double.valueOf((Integer) prop.getValue());
            Comparable<Double> res = MathOperation.ADD.evaluate(val, expVal);
            prop.updateValue(((Double) res).intValue(), world_time);
        } else if (propertyInstance.getPropertyDefinition().getType() == PropertyType.FLOAT) {
            //noinspection unchecked
            PropertyInstance<Double> prop = (PropertyInstance<Double>) propertyInstance;
            Comparable<Double> val = prop.getValue();
            Comparable<Double> res = MathOperation.ADD.evaluate(val, expVal);
            prop.updateValue(res, world_time);
        }
    }

    @Override
    public ActionDto getDto() {
        return new IncreaseActionDto(
                property,
                byExpression.toString(),
                getContextDefinition().getPrimaryEntityDefinition().getDto(),
                getContextDefinition().getSecondaryEntityDefinition() == null ? null :
                        getContextDefinition().getSecondaryEntityDefinition().getDto()
        );
    }
}
