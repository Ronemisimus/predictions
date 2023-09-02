package predictions.expression.impl;

import predictions.definition.entity.EntityDefinition;
import predictions.execution.context.Context;
import predictions.expression.api.Expression;

public class PropertyExpression<T> implements Expression<T> {
    private final String property;

    private final EntityDefinition containingEntity;

    public PropertyExpression(EntityDefinition containingEntity,
            String property) {
        this.property = property;
        this.containingEntity = containingEntity;
    }

    @Override
    public Comparable<T> evaluate(Context context) {
        return (Comparable<T>) context.getPrimaryEntityInstance().getPropertyByName(property).getValue();
    }
}
