package predictions.action.impl;

import predictions.action.api.ContextDefinition;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.entity.EntityDefinitionImpl;
import predictions.definition.environment.api.EnvVariablesManager;
import predictions.exception.*;
import predictions.expression.api.Expression;
import predictions.expression.impl.BasicBooleanExpression;
import predictions.expression.impl.BooleanComplexExpression;
import predictions.generated.PRDCondition;
import predictions.generated.PRDEntity;

import java.util.Collection;

public class ContextDefinitionImpl implements ContextDefinition {

    private final EntityDefinition primaryEntityDefinition;
    private final EntityDefinition secondaryEntityDefinition;
    private final Integer secondaryEntityAmount;
    private final Expression<Boolean> secondaryExpression;
    private final EnvVariablesManager envVariables;

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
    }

    public static ContextDefinitionImpl getInstance(PRDEntity primaryEntity,
                                 PRDEntity secondaryEntity,
                                 Integer secondaryEntityAmount,
                                 PRDCondition prdCondition,
                                 EnvVariablesManager envVariables,
                                 Collection<EntityDefinition> systemEntityDefinitions,
                                 String entity) throws BadExpressionException, MissingPropertyExpressionException, BadFunctionExpressionException, BadPropertyTypeExpressionException, NoSuchEntityActionException {
        EntityDefinition primary;
        EntityDefinition secondary = null;
        if (primaryEntity!=null)
            primary = new EntityDefinitionImpl(primaryEntity);
        else
            throw new NoSuchEntityActionException(entity);
        if (secondaryEntity!=null)
            secondary = new EntityDefinitionImpl(secondaryEntity);

        ContextDefinition contextDefinition = new ContextDefinitionImpl(
                primary,
                secondary,
                secondaryEntityAmount,
                new BasicBooleanExpression(Boolean.TRUE),
                envVariables,
                systemEntityDefinitions
        );
        return new ContextDefinitionImpl(primary,
                secondary,
                secondaryEntityAmount,
                prdCondition == null? null: new BooleanComplexExpression(
                        prdCondition,
                        contextDefinition),
                envVariables,
                systemEntityDefinitions);
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
}
