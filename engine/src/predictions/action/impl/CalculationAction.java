package predictions.action.impl;

import predictions.action.api.AbstractAction;
import predictions.action.api.ActionType;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.property.api.PropertyType;
import predictions.execution.context.Context;
import predictions.execution.instance.property.PropertyInstance;
import predictions.expression.api.Expression;
import predictions.expression.api.MathOperation;
import predictions.expression.impl.DoubleComplexExpression;
import predictions.expression.impl.DualMathExpression;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CalculationAction extends AbstractAction {

    private final String property;
    private final List<Expression<Double>> exps;
    public CalculationAction(EntityDefinition entityDefinition,
                             String property,
                             MathOperation[] ops,
                             String[] args1,
                             String[] args2) {
        super(ActionType.CALCULATION, entityDefinition);
        this.property = property;
        if (args1.length != ops.length || args2.length != ops.length) {
            throw new RuntimeException("missing arguments for calculation");
        }
        Expression<Double>[] args1Exp = (Expression<Double>[])
                Arrays.stream(args1)
                        .map(DoubleComplexExpression::new)
                        .toArray();
        Expression<Double>[] args2Exp = (Expression<Double>[])
                Arrays.stream(args2)
                        .map(DoubleComplexExpression::new)
                        .toArray();
        exps = Stream.of(ops, args1Exp, args2Exp)
                .map(t-> (Expression<Double>) new DualMathExpression(
                        (MathOperation) t[0],
                        (Expression<Double>) t[1],
                        (Expression<Double>) t[2]
                        )
                ).collect(Collectors.toList());
    }

    @Override
    public void invoke(Context context) {
        PropertyInstance<?> propertyInstance = context.getPrimaryEntityInstance().getPropertyByName(property);
        if (verifyNonNumericPropertyType(propertyInstance)) {
            throw new IllegalArgumentException("increase action can't operate on a none number property [" + property);
        }

        int world_time = context.getTick();

        if (propertyInstance.getPropertyDefinition().getType() == PropertyType.FLOAT) {
            PropertyInstance<Double> property = (PropertyInstance<Double>) propertyInstance;
            exps.forEach(t -> property.updateValue(t.evaluate(context), world_time));
        }
        else if (propertyInstance.getPropertyDefinition().getType() == PropertyType.DECIMAL) {
            PropertyInstance<Integer> property = (PropertyInstance<Integer>) propertyInstance;
            exps.forEach(t -> {
                Comparable<Double> res = t.evaluate(context);
                Comparable<Integer> res_int = ((Double)res).intValue();
                property.updateValue(res_int, world_time);
            });
        }
    }
}
