package predictions.definition.world.impl;

import dto.ReadFileDto;
import dto.subdto.read.dto.EntityErrorDto;
import dto.subdto.read.dto.RepeatEntityDto;
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
import predictions.generated.*;
import predictions.rule.api.Rule;
import predictions.termination.api.Termination;
import predictions.termination.impl.TicksTermination;
import predictions.termination.impl.TimeTermination;
import predictions.termination.impl.UserTermination;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class WorldImpl implements World {

    private final String name;
    private final EnvVariablesManager envVariablesManager;
    private final Collection<EntityDefinition> entityDefinitions;
    private final Collection<Rule> rules;
    private final Collection<Termination> terminations;
    private final Integer gridWidth;
    private final Integer gridHeight;

    private final Integer sleepTime;


    private WorldImpl(String name,
                      EnvVariablesManager envVariablesManager,
                      Collection<EntityDefinition> entityDefinitions,
                      Collection<Rule> rules,
                      Integer gridWidth,
                      Integer gridHeight,
                      Integer sleepTime) {
        this.name = name;
        this.envVariablesManager = envVariablesManager;
        this.entityDefinitions = entityDefinitions;
        this.rules = rules;
        this.terminations = new ArrayList<>();
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.sleepTime = sleepTime;
    }

    private WorldImpl(final PRDWorld res,
                      final EnvVariablesManager env,
                      final Collection<EntityDefinition> entityDefinitions,
                      final ReadFileDto.Builder builder) {
        this(res.getName(),
                env,
                entityDefinitions,
                res.getPRDRules().getPRDRule().stream()
                        .map(prdRule->
                                ConverterPRDEngine.getRuleFromPRD(prdRule,
                                        res.getPRDEntities(),
                                        env,
                                        builder
                                )
                        )
                    .collect(Collectors.toList()),
                res.getPRDGrid().getColumns(),
                res.getPRDGrid().getRows(),
                res.getSleep()
        );
    }

    public void addUserTermination() {
        this.terminations.add(new UserTermination());
    }

    public void addTicksTermination(Integer ticks) {
        this.terminations.add(new TicksTermination(ticks));
    }

    public void addTimeTermination(Duration time) {
        this.terminations.add(new TimeTermination(time));
    }

    public static World fromPRD(final PRDWorld res, final ReadFileDto.Builder builder) {
        final EnvVariablesManager env = new EnvVariableManagerImpl(res.getPRDEnvironment(), builder);
        final Collection<EntityDefinition> entityDefinitions = buildEntityDefinitions(res.getPRDEntities(), builder);
        if (res.getPRDGrid().getRows()<10 || res.getPRDGrid().getColumns()<10 ||
                res.getPRDGrid().getRows() >100 || res.getPRDGrid().getColumns() > 100) {
            builder.gridSizeError(res.getPRDGrid().getColumns(), res.getPRDGrid().getRows());
            throw new RuntimeException("Invalid grid size");
        }
        return new WorldImpl(res, env, entityDefinitions, builder);
    }

    private static Collection<EntityDefinition> buildEntityDefinitions(PRDEntities prdEntities, ReadFileDto.Builder builder) {
        List<String> entityNames = new ArrayList<>();
        return prdEntities.getPRDEntity().stream()
                .map(prdEntity -> {
                    if (entityNames.contains(prdEntity.getName()))
                    {
                        builder.entityError(
                                new EntityErrorDto.Builder()
                                        .repeatEntityError(
                                                new RepeatEntityDto(prdEntity.getName())
                                        ).build()
                        );
                        throw new RuntimeException("Duplicate entity: " + prdEntity.getName());
                    }
                    entityNames.add(prdEntity.getName());
                    return new EntityDefinitionImpl(prdEntity, builder);
                }).collect(Collectors.toList());
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

    @Override
    public int getGridWidth() {
        return this.gridWidth;
    }
    @Override
    public int getGridHeight() {
        return this.gridHeight;
    }

    @Override
    public void setTerminations(boolean userTermination, Integer ticksLimit, Integer secondsLimit) {
        this.terminations.clear();
        if (userTermination) this.terminations.add(new UserTermination());
        if (ticksLimit != null) this.terminations.add(new TicksTermination(ticksLimit));
        if (secondsLimit != null) this.terminations.add(new TimeTermination(Duration.ofSeconds(secondsLimit)));
    }

    @Override
    public World clone() {
        return new WorldImpl(
            name,
            envVariablesManager,
            entityDefinitions,
            rules,
            gridWidth,
            gridHeight,
            sleepTime
        );
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
                this.gridHeight);
    }

    public String getName() {
        return name;
    }

    @Override
    public Integer getSleepTime() {
        return sleepTime;
    }
}
