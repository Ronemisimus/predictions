package predictions.execution.context;

import predictions.execution.instance.entity.EntityInstance;
import predictions.execution.instance.property.PropertyInstance;

public interface Context {
    EntityInstance getPrimaryEntityInstance();
    void removeEntity(EntityInstance entityInstance);
    PropertyInstance getEnvironmentVariable(String name);
}
