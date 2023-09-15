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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ContextDefinitionImpl implements ContextDefinition {

    private final EntityDefinition primaryEntityDefinition;
    private final EntityDefinition secondaryEntityDefinition;
    private final Integer secondaryEntityAmount;
    private final Expression<Boolean> secondaryExpression;
    private final EnvVariablesManager envVariables;

    private final Boolean allSecondary;

    private final Collection<EntityDefinition> systemEntityDefinitions;

    private ContextDefinitionImpl(EntityDefinition primaryEntityDefinition,
                                 EntityDefinition secondaryEntityDefinition,
                                 Integer secondaryEntityAmount,
                                 Boolean allSecondary,
                                 Expression<Boolean> secondaryExpression,
                                 EnvVariablesManager envVariables,
                                 Collection<EntityDefinition> systemEntityDefinitions) {
        this.primaryEntityDefinition = primaryEntityDefinition;
        this.secondaryEntityDefinition = secondaryEntityDefinition;
        this.secondaryEntityAmount = secondaryEntityAmount;
        this.secondaryExpression = secondaryExpression;
        this.envVariables = envVariables;
        this.allSecondary = allSecondary;
        this.systemEntityDefinitions = systemEntityDefinitions;
    }

    public static ContextDefinition getInstance(PRDEntity primaryEntity,
                                                    PRDEntity secondaryEntity,
                                                    String secondaryEntityAmount,
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

        Integer amount = null;
        Boolean allSecondary = false;
        if (secondaryEntity!=null && secondaryEntityAmount!=null) {
            try {
                amount = Integer.valueOf(secondaryEntityAmount);
                if (amount < 0) {
                    actionBuilder.badSelectionCount(secondaryEntityAmount);
                    throw new RuntimeException("bad selection count");
                }
            } catch (Exception e) {
                amount = secondaryEntityAmount.equalsIgnoreCase("all") ? -1 : null;
                if (amount == null) {
                    actionBuilder.badSelectionCount(secondaryEntityAmount);
                    throw new RuntimeException("bad selection count");
                } else {
                    allSecondary = true;
                }
            }
        }

        ContextDefinition contextDefinition = new ContextDefinitionImpl(
                primary,
                secondary,
                amount,
                allSecondary,
                new BasicBooleanExpression(Boolean.TRUE),
                envVariables,
                systemEntityDefinitions
        );
        ExpressionErrorDto.Builder builderExpression = new ExpressionErrorDto.Builder();
        try {
            return new ContextDefinitionImpl(primary,
                    secondary,
                    amount,
                    allSecondary,
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
        if (allSecondary){
            return getSecondaryEntityDefinition().getPopulation();
        }
        return secondaryEntityAmount;
    }

    @Override
    public Expression<Boolean> getSecondaryExpression() {
        return secondaryExpression;
    }

    @Override
    public Boolean getSecondaryEntityAll() {
        return allSecondary;
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
                        .filter(entity-> entity.getId() != entityInstance.getId()) // an instance cannot be both primary and secondary
                        .filter(entity-> entity.getEntityTypeName().equals(secondaryEntityDefinition.getName()))
                        .filter( entity -> allSecondary || secondaryExpression==null || secondaryExpression.evaluate(new ContextImpl(
                            entityInstance,
                            entity,
                            entityInstanceManager,
                            activeEnvironment,
                            this,
                            tick)).equals(true))
                        .collect(Collectors.toList());

        if (secondaryEntityDefinition == null) {
            return Collections.singletonList(
                    new ContextImpl(entityInstance,
                            null,
                            entityInstanceManager,
                            activeEnvironment,
                            this,
                            tick)
            );
        } else {
            List<Context> list = new ArrayList<>();
            int bound = getSecondaryEntityRealAmount();
            if (allSecondary || (bound != getSecondaryEntityAmount() && bound!=0))
            {
                list.addAll(secondaryEntities.stream()
                        .map(entity -> new ContextImpl(
                                entityInstance,
                                entity,
                                entityInstanceManager,
                                activeEnvironment,
                                this,
                                tick
                        )).collect(Collectors.toList()));
            }
            else{
                for (int i = 0; i < bound; i++) {
                    ContextImpl context = new ContextImpl(
                            entityInstance,
                            secondaryEntities.isEmpty() ? null :
                                    secondaryEntities.get((int) (Math.random() * secondaryEntities.size())),
                            entityInstanceManager,
                            activeEnvironment,
                            this,
                            tick
                    );
                    list.add(context);
                }
            }
            return list;
        }

    }

    private int getSecondaryEntityRealAmount() {
        if(secondaryEntityAmount==null)
        {
            return 0;
        }
        else if (secondaryEntityAmount!=-1 && secondaryEntityAmount<secondaryEntityDefinition.getPopulation()){
            return secondaryEntityAmount;
        }
        else
        {
            return secondaryEntityDefinition.getPopulation();
        }
    }
}
