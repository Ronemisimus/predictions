package predictions.execution.instance.entity.manager;

import predictions.definition.entity.EntityDefinition;
import predictions.execution.instance.entity.EntityInstance;

import java.util.List;

public interface EntityInstanceManager {

    EntityInstance create(EntityDefinition entityDefinition);
    List<EntityInstance> getInstances();

    void initializeGrid(int gridWidth, int gridHeight);

    void moveEntities();

    void killEntity(int id);

    void replaceEntity(int id, EntityDefinition kill, EntityDefinition create, Boolean derived);

    void finishKills();

    void finishReplace(int tick);
}
