package predictions.definition.world.impl;

import predictions.definition.entity.EntityDefinition;
import predictions.definition.environment.api.EnvVariablesManager;
import predictions.definition.world.api.World;
import predictions.rule.api.Rule;
import predictions.termination.api.Termination;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

public class WorldImpl implements World {

    private EnvVariablesManager envVariablesManager;
    private Collection<EntityDefinition> entityDefinitions;
    private Collection<Rule> rules;
    private Collection<Termination> terminations;


    public WorldImpl(EnvVariablesManager envVariablesManager,
                     Collection<EntityDefinition> entityDefinitions,
                     Collection<Rule> rules,
                     Collection<Termination> terminations) {
        this.envVariablesManager = envVariablesManager;
        this.entityDefinitions = entityDefinitions;
        this.rules = rules;
        this.terminations = terminations;
    }

    @Override
    public EnvVariablesManager getEnvVariablesManager() {
        return envVariablesManager;
    }

    @Override
    public Optional<EntityDefinition> getEntityDefinitionByName(String name) {
        return entityDefinitions.stream().filter(e -> e.getName().equals(name)).findFirst();
    }

    @Override
    public Iterator<EntityDefinition> getEntityDefinitions() {
        return entityDefinitions.iterator();
    }

    @Override
    public Iterator<Rule> getRules() {
        return rules.iterator();
    }

    @Override
    public Iterator<Termination> getTerminations() {
        return terminations.iterator();
    }
}
