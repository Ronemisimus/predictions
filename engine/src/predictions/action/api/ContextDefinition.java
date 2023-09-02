package predictions.action.api;

import predictions.definition.entity.EntityDefinition;
import predictions.definition.environment.api.EnvVariablesManager;
import predictions.expression.api.Expression;

public interface ContextDefinition {

    EntityDefinition getPrimaryEntityDefinition();
    EntityDefinition getSecondaryEntityDefinition();
    Integer getSecondaryEntityAmount();
    Expression<Boolean> getSecondaryExpression();
    EnvVariablesManager getEnvVariables();
}
