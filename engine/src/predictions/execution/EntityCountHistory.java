package predictions.execution;

import java.util.*;
import java.util.stream.Collectors;

public class EntityCountHistory {
    private final Map<Integer, Integer> entityCount;

    public EntityCountHistory() {
        this.entityCount = new HashMap<>();
    }

    public Map<Integer, Integer> getEntityCount() {
        if (entityCount.size() <= 10000) {
            return entityCount;
        }

        int targetSampleSize = 10000;
        int stepSize = entityCount.size() / targetSampleSize;

        return entityCount.entrySet().stream()
                .filter(entry -> entry.getKey() % stepSize == 0)
                .limit(targetSampleSize)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void addEntityCount(int count) {
        entityCount.put(entityCount.size(), count);
    }
}
