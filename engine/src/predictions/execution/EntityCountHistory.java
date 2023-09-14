package predictions.execution;

import java.util.HashMap;
import java.util.Map;

public class EntityCountHistory {
    private Map<Integer, Integer> entityCount;

    public EntityCountHistory() {
        this.entityCount = new HashMap<>();
    }

    public Map<Integer, Integer> getEntityCount() {
        return entityCount;
    }

    public void addEntityCount(int count) {
        entityCount.put(entityCount.size(), count);
    }
}
