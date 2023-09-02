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
    private final Integer gridWidth;
    private final Integer gridHeight;
    private final Integer threadCount;


    private WorldImpl(EnvVariablesManager envVariablesManager,
                      Collection<EntityDefinition> entityDefinitions,
                      Collection<Rule> rules,
                      Collection<Termination> terminations,
                      Integer gridWidth,
                      Integer gridHeight,
                      Integer threadCount) {
        this.envVariablesManager = envVariablesManager;
        this.entityDefinitions = entityDefinitions;
        this.rules = rules;
        this.terminations = terminations;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.threadCount = threadCount;
    }

    private WorldImpl(final PRDWorld res, final EnvVariablesManager env) throws RuntimeException {
        this(env,
                res.getPRDEntities().getPRDEntity().stream()
                        .map(EntityDefinitionImpl::new).collect(Collectors.toList()),
                res.getPRDRules().getPRDRule().stream().map(prdRule->
                        {
                            try {
                                return ConverterPRDEngine.getRuleFromPRD(prdRule, res.getPRDEntities(), env);
                            } catch (final BadExpressionException | BadFunctionExpressionException |
                                           MissingPropertyExpressionException | BadPropertyTypeExpressionException |
                                           MissingPropertyActionException | NoSuchEntityActionException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .collect(Collectors.toList()),
                res.getPRDTermination().getPRDBySecondOrPRDByTicks().stream().map(
                        prdTermination -> (prdTermination instanceof PRDByTicks)?
                                new TicksTermination((PRDByTicks)prdTermination) :
                                new TimeTermination((PRDBySecond)prdTermination)).collect(Collectors.toList()),
                res.getPRDGrid().getColumns(),
                res.getPRDGrid().getRows(),
                res.getPRDThreadCount());
    }

    public static World fromPRD(final PRDWorld res) throws RepeatNameException, RuntimeException {
        final EnvVariablesManager env = new EnvVariableManagerImpl(res.getPRDEnvironment());
        return new WorldImpl(res, env);
    }

    @Override
    public EnvVariablesManager getEnvVariablesManager() {
        return this.envVariablesManager;
    }

    @Override
    public Optional<EntityDefinition> getEntityDefinitionByName(final String name) {
        return this.entityDefinitions.stream().filter(e -> e.getName().equals(name)).findFirst();
    }

    @Override
    public Iterator<EntityDefinition> getEntityDefinitions() {
        return this.entityDefinitions.iterator();
    }

    @Override
    public Iterator<Rule> getRules() {
        return this.rules.iterator();
    }

    @Override
    public Iterator<Termination> getTerminations() {
        return this.terminations.iterator();
    }

    public Integer getGridWidth() {
        return this.gridWidth;
    }

    public Integer getGridHeight() {
        return this.gridHeight;
    }

    public Integer getThreadCount() {
        return this.threadCount;
    }

    @Override
    public WorldDto getDto() {
        final List<PropertyDto> env = this.envVariablesManager.getEnvVariables().stream().map(PropertyDefinition::getDto).collect(Collectors.toList());
        final List<EntityDto> entities = this.entityDefinitions.stream().map(EntityDefinition::getDto).collect(Collectors.toList());
        final List<RuleDto> res = this.rules.stream().map(Rule::getDto).collect(Collectors.toList());
        final Integer tickTermination = this.terminations.stream()
                .filter(termination -> termination instanceof TicksTermination)
                .findFirst().map(t -> ((TicksTermination)t).getTicks()).orElse(null);
        final Duration timeTermination = this.terminations.stream()
                .filter(termination -> termination instanceof TimeTermination)
                .findFirst().map(t -> ((TimeTermination)t).getTerminationDuration()).orElse(null);
        final Integer timeTerminationInteger = null == timeTermination ? null : Math.toIntExact(timeTermination.getSeconds());
        final boolean userTermination = this.terminations.stream().anyMatch(termination -> termination instanceof UserTermination);
        return new WorldDto(
                env,
                entities,
                res,
                tickTermination,
                timeTerminationInteger,
                userTermination,
                this.gridWidth,
                this.gridHeight,
                this.threadCount);
    }
}
