package predictions.action.impl;

import dto.subdto.read.dto.rule.ActionErrorDto;
import dto.subdto.read.dto.rule.ExpressionErrorDto;
import dto.subdto.show.world.action.ActionDto;
import dto.subdto.show.world.action.CalculationActionDto;
import predictions.ConverterPRDEngine;
import predictions.action.api.AbstractAction;
import predictions.action.api.ActionType;
import predictions.action.api.ContextDefinition;
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
import predictions.expression.impl.DualMathExpression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CalculationAction extends AbstractAction {

    private final Boolean propInSecondary;
    private final String property;
    private final List<Expression<Double>> exps;
    public CalculationAction(ContextDefinition contextDefinition,
                             String entityName,
                             String property,
                             MathOperation[] ops,
                             String[] args1,
                             String[] args2,
                             ActionErrorDto.Builder builder) {
        super(ActionType.CALCULATION, contextDefinition);
        ExpressionErrorDto.Builder expBuilder = new ExpressionErrorDto.Builder();
        this.property = property;
        this.propInSecondary = contextDefinition.getSecondaryEntityDefinition()!=null &&
                contextDefinition.getSecondaryEntityDefinition().getName().equals(entityName);
        Optional<PropertyDefinition<?>> prop = ConverterPRDEngine.checkEntityAndPropertyInContext(entityName, property, contextDefinition, builder);

        if ( prop.isPresent() && verifyNonNumericPropertyType(prop.get())) {
            builder.propertyTypeMismatch(property, ActionType.CALCULATION.name());
            throw new IllegalArgumentException("increase action can't operate on a none number property [" + property);
        }

        if (args1.length != ops.length || args2.length != ops.length) {
            throw new RuntimeException("missing arguments for calculation");
        }
        List<Expression<Double>> args1Exp = Arrays.stream(args1)
                        .map(exp -> {
                            try {
                                return ExpressionBuilder.buildDoubleExpression(exp, contextDefinition, expBuilder);
                            } catch (Exception e) {
                                builder.expressionError(expBuilder.build());
                                throw e;
                            }
                        }).collect(Collectors.toList());
        List<Expression<Double>> args2Exp = Arrays.stream(args2)
                .map(exp -> {
                    try {
                        return ExpressionBuilder.buildDoubleExpression(exp, contextDefinition, expBuilder);
                    } catch (Exception e) {
                        builder.expressionError(expBuilder.build());
                        throw e;
                    }
                }).collect(Collectors.toList());
        exps = new ArrayList<>();
        for (int i=0;i<ops.length;i++) {
            exps.add(new DualMathExpression(ops[i], args1Exp.get(i), args2Exp.get(i)));
        }
    }

    @Override
    public void invoke(Context context) {
        PropertyInstance<?> propertyInstance = propInSecondary? context.getSecondaryEntityInstance().getPropertyByName(property):
                context.getPrimaryEntityInstance().getPropertyByName(property);

        int world_time = context.getTick();

        if (propertyInstance.getPropertyDefinition().getType() == PropertyType.FLOAT) {
            PropertyInstance<Double> property = (PropertyInstance<Double>) propertyInstance;
            exps.forEach(t -> property.updateValue(t.evaluate(context), world_time));
        }
        else if (propertyInstance.getPropertyDefinition().getType() == PropertyType.DECIMAL) {
            PropertyInstance<Integer> property = (PropertyInstance<Integer>) propertyInstance;
            exps.stream().map(t -> t.evaluate(context)).forEach(res -> property.updateValue(res, world_time));
        }
    }

    @Override
    public ActionDto getDto() {
        return new CalculationActionDto(
                getContextDefinition().getPrimaryEntityDefinition().getDto(),
                getContextDefinition().getSecondaryEntityDefinition() == null?null:
                        getContextDefinition().getSecondaryEntityDefinition().getDto(),
                property,
                exps.toString()
        );
    }
}
