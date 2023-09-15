package predictions.execution.instance.entity.manager;

import predictions.definition.entity.EntityDefinition;
import predictions.execution.EntityCountHistory;
import predictions.execution.instance.entity.EntityInstance;

import java.util.List;
import java.util.Map;

public interface EntityInstanceManager {

    EntityInstance create(EntityDefinition entityDefinition);
    List<EntityInstance> getInstances();

    void initializeGrid(int gridWidth, int gridHeight);

    void moveEntities();

    void printGrid();

    void killEntity(int id);

    void replaceEntity(int id, EntityDefinition kill, EntityDefinition create, Boolean derived);

    void finishKills();

    void finishReplace(int tick);

    Map<String, EntityCountHistory> getEntityCounts();

    void updateEntityCounts();
}
