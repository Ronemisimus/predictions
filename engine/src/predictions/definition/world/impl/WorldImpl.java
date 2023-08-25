package predictions.definition.world.impl;

import dto.subdto.show.world.EntityDto;
import dto.subdto.show.world.PropertyDto;
import dto.subdto.show.world.RuleDto;
import dto.subdto.show.world.WorldDto;
import predictions.ConverterPRDEngine;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.entity.EntityDefinitionImpl;
import predictions.definition.environment.api.EnvVariablesManager;
import predictions.definition.environment.impl.EnvVariableManagerImpl;
import predictions.definition.property.api.PropertyDefinition;
import predictions.definition.world.api.World;
import predictions.exception.*;
import predictions.generated.PRDBySecond;
import predictions.generated.PRDByTicks;
import predictions.generated.PRDWorld;
import predictions.rule.api.Rule;
import predictions.termination.api.Termination;
import predictions.termination.impl.TicksTermination;
import predictions.termination.impl.TimeTermination;
import predictions.termination.impl.UserTermination;

import java.time.Duration;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class WorldImpl implements World {

    private final EnvVariablesManager envVariablesManager;
    private final Collection<EntityDefinition> entityDefinitions;
    private final Collection<Rule> rules;
    private final Collection<Termination> terminations;


    private WorldImpl(EnvVariablesManager envVariablesManager,
                     Collection<EntityDefinition> entityDefinitions,
                     Collection<Rule> rules,
                     Collection<Termination> terminations) {
        this.envVariablesManager = envVariablesManager;
        this.entityDefinitions = entityDefinitions;
        this.rules = rules;
        this.terminations = terminations;
    }

    private WorldImpl(PRDWorld res, EnvVariablesManager env) throws RuntimeException {
        this(env,
                res.getPRDEntities().getPRDEntity().stream()
                        .map(EntityDefinitionImpl::new).collect(Collectors.toList()),
                res.getPRDRules().getPRDRule().stream().map(prdRule->
                        {
                            try {
                                return ConverterPRDEngine.getRuleFromPRD(prdRule, res.getPRDEntities(), env);
                            } catch (BadExpressionException | BadFunctionExpressionException |
                                     MissingPropertyExpressionException | BadPropertyTypeExpressionException |
                                     MissingPropertyActionException | NoSuchEntityActionException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .collect(Collectors.toList()),
                res.getPRDTermination().getPRDByTicksOrPRDBySecond().stream().map(
                        prdTermination -> (prdTermination instanceof PRDByTicks)?
                                new TicksTermination((PRDByTicks)prdTermination) :
                                new TimeTermination((PRDBySecond)prdTermination)).collect(Collectors.toList()));
    }

    public static World fromPRD(PRDWorld res) throws RepeatNameException, RuntimeException {
        EnvVariablesManager env = new EnvVariableManagerImpl(res.getPRDEvironment());
        return new WorldImpl(res, env);
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

    @Override
    public WorldDto getDto() {
        List<PropertyDto> env = envVariablesManager.getEnvVariables().stream().map(PropertyDefinition::getDto).collect(Collectors.toList());
        List<EntityDto> entities = entityDefinitions.stream().map(EntityDefinition::getDto).collect(Collectors.toList());
        List<RuleDto> res = rules.stream().map(Rule::getDto).collect(Collectors.toList());
        Integer tickTermination = terminations.stream()
                .filter(termination -> termination instanceof TicksTermination)
                .findFirst().map(t -> ((TicksTermination)t).getTicks()).orElse(null);
        Duration timeTermination = terminations.stream()
                .filter(termination -> termination instanceof TimeTermination)
                .findFirst().map(t -> ((TimeTermination)t).getTerminationDuration()).orElse(null);
        Integer timeTerminationInteger = timeTermination == null ? null : Math.toIntExact(timeTermination.getSeconds());
        boolean userTermination = terminations.stream().anyMatch(termination -> termination instanceof UserTermination);
        return new WorldDto(env,entities, res, tickTermination, timeTerminationInteger, userTermination);
    }
}
