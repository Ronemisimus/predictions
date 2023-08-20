package predictions.exception;

import predictions.definition.entity.EntityDefinition;

public class MissingPropertyExpressionException extends Throwable {

    private final String finalExpression;
    private final EntityDefinition entityDefinition;
    private final boolean environment;
    public MissingPropertyExpressionException(String finalExpression, EntityDefinition entityDefinition, boolean environment) {
        this.entityDefinition = entityDefinition;
        this.finalExpression = finalExpression;
        this.environment = environment;
    }

    public String getFinalExpression() {
        return finalExpression;
    }

    public EntityDefinition getEntityDefinition() {
        return entityDefinition;
    }

    public boolean isEnvironment() {
        return environment;
    }
}
