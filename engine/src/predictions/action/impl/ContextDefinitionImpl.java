package predictions.action.impl;

import dto.ReadFileDto;
import dto.subdto.read.dto.rule.ActionErrorDto;
import dto.subdto.read.dto.rule.ExpressionErrorDto;
import dto.subdto.read.dto.rule.RuleErrorDto;
import predictions.action.api.ContextDefinition;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.entity.EntityDefinitionImpl;
import predictions.definition.environment.api.EnvVariablesManager;
import predictions.execution.context.Context;
import predictions.execution.context.ContextImpl;
import predictions.execution.instance.entity.EntityInstance;
import predictions.execution.instance.entity.manager.EntityInstanceManager;
import predictions.execution.instance.environment.api.ActiveEnvironment;
import predictions.expression.api.Expression;
import predictions.expression.impl.BasicBooleanExpression;
import predictions.expression.impl.BooleanComplexExpression;
import predictions.generated.PRDCondition;
import predictions.generated.PRDEntity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ContextDefinitionImpl implements ContextDefinition {

    private final EntityDefinition primaryEntityDefinition;
    private final EntityDefinition secondaryEntityDefinition;
    private final Integer secondaryEntityAmount;
    private final Expression<Boolean> secondaryExpression;
    private final EnvVariablesManager envVariables;

    private final Boolean randomChoice;

    private final Collection<EntityDefinition> systemEntityDefinitions;

    private ContextDefinitionImpl(EntityDefinition primaryEntityDefinition,
                                 EntityDefinition secondaryEntityDefinition,
                                 Integer secondaryEntityAmount,
                                 Expression<Boolean> secondaryExpression,
                                 EnvVariablesManager envVariables,
                                 Collection<EntityDefinition> systemEntityDefinitions) {
        this.primaryEntityDefinition = primaryEntityDefinition;
        this.secondaryEntityDefinition = secondaryEntityDefinition;
        this.secondaryEntityAmount = secondaryEntityAmount;
        this.secondaryExpression = secondaryExpression;
        this.envVariables = envVariables;
        this.systemEntityDefinitions = systemEntityDefinitions;
        this.randomChoice = secondaryEntityDefinition != null &&
                getSecondaryEntityRealAmount() < secondaryEntityDefinition.getPopulation();
    }

    public static ContextDefinition getInstance(PRDEntity primaryEntity,
                                                    PRDEntity secondaryEntity,
                                                    Integer secondaryEntityAmount,
                                                    PRDCondition prdCondition,
                                                    EnvVariablesManager envVariables,
                                                    Collection<EntityDefinition> systemEntityDefinitions,
                                                    String entity,
                                                    RuleErrorDto.Builder builder,
                                                    ReadFileDto.Builder bigBuilder,
                                                    ActionErrorDto.Builder actionBuilder) {
        EntityDefinition primary;
        EntityDefinition secondary = null;
        if (primaryEntity!=null)
            primary = new EntityDefinitionImpl(primaryEntity, bigBuilder);
        else {
            if (entity!=null) {
                actionBuilder.entityNotInContext(entity);
                throw new RuntimeException("Entity " + entity + " not found");
            }
            else
            {
                actionBuilder.noPrimaryEntity();
                throw new RuntimeException("No primary entity");
            }
        }
        if (secondaryEntity!=null)
            secondary = new EntityDefinitionImpl(secondaryEntity, bigBuilder);

        ContextDefinition contextDefinition = new ContextDefinitionImpl(
                primary,
                secondary,
                secondaryEntityAmount,
                new BasicBooleanExpression(Boolean.TRUE),
                envVariables,
                systemEntityDefinitions
        );
        ExpressionErrorDto.Builder builderExpression = new ExpressionErrorDto.Builder();
        try {
            return new ContextDefinitionImpl(primary,
                    secondary,
                    secondaryEntityAmount,
                    prdCondition == null ? null :
                            new BooleanComplexExpression(
                                    prdCondition,
                                    contextDefinition,
                                    builderExpression
                            ),
                    envVariables,
                    systemEntityDefinitions);
        }catch (Exception e)
        {
            builder.expressionError(builderExpression.build());
            throw e;
        }
    }

                                 @Override
    public EntityDefinition getPrimaryEntityDefinition() {
        return primaryEntityDefinition;
    }

    @Override
    public EntityDefinition getSecondaryEntityDefinition() {
        return secondaryEntityDefinition;
    }

    @Override
    public Collection<EntityDefinition> getSystemEntityDefinitions() {
        return systemEntityDefinitions;
    }

    @Override
    public Integer getSecondaryEntityAmount() {
        return secondaryEntityAmount;
    }

    @Override
    public Expression<Boolean> getSecondaryExpression() {
        return secondaryExpression;
    }

    @Override
    public EnvVariablesManager getEnvVariables() {
        return envVariables;
    }

    @Override
    public Collection<Context> getContextList(EntityInstance entityInstance, List<EntityInstance> entityInstances,
                                              EntityInstanceManager entityInstanceManager,
                                              ActiveEnvironment activeEnvironment, Integer tick) {

        if (!primaryEntityDefinition.isInstance(entityInstance)) return new ArrayList<>();

        List<EntityInstance> secondaryEntities = secondaryEntityDefinition == null? new ArrayList<>():
                entityInstances.stream()
                .filter(entity-> entity.getEntityTypeName().equals(secondaryEntityDefinition.getName()))
                .filter( entity -> secondaryExpression==null || secondaryExpression.evaluate(new ContextImpl(
                        entityInstance,
                        entity,
                        entityInstanceManager,
                        activeEnvironment,
                        this,
                        tick)).equals(true))
                .collect(Collectors.toList());

        return secondaryEntityDefinition==null? Collections.singletonList(
                new ContextImpl(entityInstance,
                        null,
                        entityInstanceManager,
                        activeEnvironment,
                        this,
                        tick)
        ) :
        IntStream.range(0, getSecondaryEntityRealAmount())
                .mapToObj(i -> new ContextImpl(
                    entityInstance,
                    secondaryEntities.isEmpty()?null:
                            secondaryEntities.get(randomChoice? (int)(Math.random() * secondaryEntities.size()): i),
                    entityInstanceManager,
                    activeEnvironment,
                    this,
                    tick
                ))
                .collect(Collectors.toList());

    }

    private int getSecondaryEntityRealAmount() {
        if(secondaryEntityAmount==null)
        {
            return secondaryEntityDefinition.getPopulation();
        }
        else if (secondaryEntityAmount<secondaryEntityDefinition.getPopulation()){
            return secondaryEntityAmount;
        }
        else
        {
            return secondaryEntityDefinition.getPopulation();
        }
    }
}
