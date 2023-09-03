package predictions.action.api;

import predictions.definition.entity.EntityDefinition;
import predictions.definition.environment.api.EnvVariablesManager;
import predictions.expression.api.Expression;

import java.util.Collection;

public interface ContextDefinition {

    EntityDefinition getPrimaryEntityDefinition();
    EntityDefinition getSecondaryEntityDefinition();
    Collection<EntityDefinition> getSystemEntityDefinitions();
    Integer getSecondaryEntityAmount();
    Expression<Boolean> getSecondaryExpression();
    EnvVariablesManager getEnvVariables();
}
