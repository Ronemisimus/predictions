package predictions.execution.context;

import predictions.definition.entity.EntityDefinition;
import predictions.execution.instance.entity.EntityInstance;
import predictions.execution.instance.property.PropertyInstance;

public interface Context {
    EntityInstance getPrimaryEntityInstance();

    EntityInstance getSecondaryEntityInstance();
    void removeEntity(EntityInstance entityInstance);
    PropertyInstance<?> getEnvironmentVariable(String name);

    EntityInstance createEntity(EntityDefinition entityDefinition);

    int getTick();

    void replaceEntity(int id, EntityDefinition killEntity, EntityDefinition createEntity, boolean derived);
}
