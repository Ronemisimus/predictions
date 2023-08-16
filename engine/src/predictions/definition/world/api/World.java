package predictions.definition.world.api;

import predictions.definition.entity.EntityDefinition;
import predictions.definition.environment.api.EnvVariablesManager;
import predictions.rule.api.Rule;
import predictions.termination.api.Termination;

import java.util.Iterator;
import java.util.Optional;

public interface World {
    EnvVariablesManager getEnvVariablesManager();
    Optional<EntityDefinition> getEntityDefinitionByName(String name);

    Iterator<EntityDefinition> getEntityDefinitions();

    Iterator<Rule> getRules();

    Iterator<Termination> getTerminations();
}
