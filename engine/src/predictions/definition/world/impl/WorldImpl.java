package predictions.definition.world.impl;

import dto.ReadFileDto;
import dto.subdto.read.dto.EntityErrorDto;
import dto.subdto.read.dto.RepeatEntityDto;
import dto.subdto.read.dto.TerminationBadDto;
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

    private WorldImpl(final PRDWorld res,
                      final EnvVariablesManager env,
                      final Collection<EntityDefinition> entityDefinitions,
                      final ReadFileDto.Builder builder) {
        this(env,
            entityDefinitions,
            res.getPRDRules().getPRDRule().stream().map(prdRule-> ConverterPRDEngine.getRuleFromPRD(prdRule, res.getPRDEntities(), env, builder))
                    .collect(Collectors.toList()),
            createTerminations(res.getPRDTermination(),builder),
            res.getPRDGrid().getColumns(),
            res.getPRDGrid().getRows(),
            res.getPRDThreadCount());
    }

    private static Collection<Termination> createTerminations(PRDTermination prdTermination, ReadFileDto.Builder builder) {
        List<Termination> res = new ArrayList<>();
        if (prdTermination.getPRDByUser()!=null)
        {
            res.add(new UserTermination());
        }
        if (prdTermination.getPRDBySecondOrPRDByTicks()!=null)
        {
            prdTermination.getPRDBySecondOrPRDByTicks()
                    .forEach(prdBySecondOrPRDByTicks ->{
                        Integer countBySeconds = prdBySecondOrPRDByTicks instanceof PRDBySecond? ((PRDBySecond)prdBySecondOrPRDByTicks).getCount():null;
                        Integer countByTicks = prdBySecondOrPRDByTicks instanceof PRDByTicks? ((PRDByTicks)prdBySecondOrPRDByTicks).getCount():null;
                        boolean badCount = (countBySeconds!=null && countBySeconds<=0) ||
                                (countByTicks!=null && countByTicks<=0);
                        if (badCount)
                        {
                            builder.terminationError(
                                    new TerminationBadDto(countBySeconds,countByTicks)
                            );
                            throw new RuntimeException("Invalid termination count " + prdBySecondOrPRDByTicks);
                        }

                        res.add(prdBySecondOrPRDByTicks instanceof PRDByTicks?
                                        new TicksTermination((PRDByTicks)prdBySecondOrPRDByTicks):
                                prdBySecondOrPRDByTicks instanceof PRDBySecond?
                                        new TimeTermination((PRDBySecond)prdBySecondOrPRDByTicks):
                                null);
                    });
        }
        return res;
    }

    public static World fromPRD(final PRDWorld res, final ReadFileDto.Builder builder) {
        final EnvVariablesManager env = new EnvVariableManagerImpl(res.getPRDEnvironment(), builder);
        final Collection<EntityDefinition> entityDefinitions = buildEntityDefinitions(res.getPRDEntities(), builder);
        if (res.getPRDThreadCount()<=0) {
            builder.badThreadCountError();
            throw new RuntimeException("Invalid thread count");
        }
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
