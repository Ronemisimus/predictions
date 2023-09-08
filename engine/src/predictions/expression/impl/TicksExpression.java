package predictions.expression.impl;

import predictions.definition.entity.EntityDefinition;
import predictions.execution.context.Context;
import predictions.expression.api.Expression;

public class TicksExpression implements Expression<Double> {

    private final String property;

    private final EntityDefinition selectedEntity;

    public TicksExpression(String property, EntityDefinition selectedEntity) {
        this.property = property;
        this.selectedEntity = selectedEntity;
    }

    @Override
    public Comparable<Double> evaluate(Context context) {
        return (double)(context.getTick() - context.getPrimaryEntityInstance().getPropertyByName(property).getTimeModification());
    }

    @Override
    public String toString() {
        return "ticks(" + selectedEntity.getName() + "." + property + ")";
    }
}
