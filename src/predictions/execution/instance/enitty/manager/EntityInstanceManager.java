package predictions.execution.instance.enitty.manager;

import predictions.definition.entity.EntityDefinition;
import predictions.execution.instance.enitty.EntityInstance;

import java.util.List;

public interface EntityInstanceManager {

    EntityInstance create(EntityDefinition entityDefinition);
    List<EntityInstance> getInstances();

    void killEntity(int id);
}
