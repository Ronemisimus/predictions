package predictions.execution.context;

import predictions.action.api.ContextDefinition;
import predictions.definition.entity.EntityDefinition;
import predictions.execution.instance.entity.EntityInstance;
import predictions.execution.instance.entity.manager.EntityInstanceManager;
import predictions.execution.instance.environment.api.ActiveEnvironment;
import predictions.execution.instance.property.PropertyInstance;

public class ContextImpl implements Context {

    private final EntityInstance primaryEntityInstance;
    private final EntityInstanceManager entityInstanceManager;
    private final ActiveEnvironment activeEnvironment;

    private final ContextDefinition contextDefinition;

    private final EntityInstance secondaryEntityInstance;
    private final int tick;

    public ContextImpl(EntityInstance primaryEntityInstance,
                       EntityInstance secondaryEntityInstance,
                       EntityInstanceManager entityInstanceManager,
                       ActiveEnvironment activeEnvironment,
                       ContextDefinition contextDefinition,
                       int tick) {
        this.primaryEntityInstance = primaryEntityInstance;
        this.entityInstanceManager = entityInstanceManager;
        this.activeEnvironment = activeEnvironment;
        this.contextDefinition = contextDefinition;
        this.secondaryEntityInstance = secondaryEntityInstance;
        this.tick = tick;
    }

    @Override
    public EntityInstance getPrimaryEntityInstance() {
        return primaryEntityInstance;
    }

    @Override
    public void removeEntity(EntityInstance entityInstance) {
        entityInstanceManager.killEntity(entityInstance.getId());
    }

    public ContextDefinition getContextDefinition() {
        return contextDefinition;
    }

    @Override
    public EntityInstance getSecondaryEntityInstance() {
        return secondaryEntityInstance;
    }

    @Override
    public PropertyInstance<?> getEnvironmentVariable(String name) {
        return activeEnvironment.getProperty(name);
    }

    @Override
    public EntityInstance createEntity(EntityDefinition entityDefinition) {
        return entityInstanceManager.create(entityDefinition);
    }

    @Override
    public int getTick() {
        return tick;
    }
}
