package predictions.execution.context;

import predictions.execution.instance.entity.EntityInstance;
import predictions.execution.instance.entity.manager.EntityInstanceManager;
import predictions.execution.instance.environment.api.ActiveEnvironment;
import predictions.execution.instance.property.PropertyInstance;

public class ContextImpl implements Context {

    private final EntityInstance primaryEntityInstance;
    private final EntityInstanceManager entityInstanceManager;
    private final ActiveEnvironment activeEnvironment;

    private final int tick;

    public ContextImpl(EntityInstance primaryEntityInstance, EntityInstanceManager entityInstanceManager, ActiveEnvironment activeEnvironment, int tick) {
        this.primaryEntityInstance = primaryEntityInstance;
        this.entityInstanceManager = entityInstanceManager;
        this.activeEnvironment = activeEnvironment;
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

    @Override
    public PropertyInstance<?> getEnvironmentVariable(String name) {
        return activeEnvironment.getProperty(name);
    }

    @Override
    public int getTick() {
        return tick;
    }
}
