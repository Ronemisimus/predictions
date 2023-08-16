package predictions.execution.instance.entity.manager;

import predictions.definition.entity.EntityDefinition;
import predictions.execution.instance.entity.EntityInstance;

import java.util.List;

public interface EntityInstanceManager {

    EntityInstance create(EntityDefinition entityDefinition);
    List<EntityInstance> getInstances();

    void killEntity(int id);
}
