package predictions.action.api;

import predictions.definition.entity.EntityDefinition;
import predictions.definition.environment.api.EnvVariablesManager;
import predictions.execution.context.Context;
import predictions.execution.instance.entity.EntityInstance;
import predictions.execution.instance.entity.manager.EntityInstanceManager;
import predictions.execution.instance.environment.api.ActiveEnvironment;
import predictions.expression.api.Expression;

import java.util.Collection;
import java.util.List;

public interface ContextDefinition {

    EntityDefinition getPrimaryEntityDefinition();
    EntityDefinition getSecondaryEntityDefinition();
    Collection<EntityDefinition> getSystemEntityDefinitions();
    Integer getSecondaryEntityAmount();
    Expression<Boolean> getSecondaryExpression();
    EnvVariablesManager getEnvVariables();

    Collection<Context> getContextList(EntityInstance entityInstance, List<EntityInstance> entityInstances,
                                       EntityInstanceManager entityInstanceManager,
                                       ActiveEnvironment activeEnvironment, Integer tick);
}
