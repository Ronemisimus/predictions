package predictions.execution;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EntityCountHistory implements Cloneable {
    private final Map<Integer,Integer> entityCount;

    private static final int MAX_HISTORY_SIZE = 100;

    private int latestTick;

    public EntityCountHistory() {
        this.entityCount = new HashMap<>();
        this.latestTick = -1;
    }

    private EntityCountHistory(Map<Integer,Integer> entityCount, int latestTick) {
        this.entityCount = new HashMap<>(entityCount);
        this.latestTick = latestTick;
    }

    public Map<Integer, Integer> getEntityCount(int tick) {
        Map<Integer,Integer> snapshot;
        synchronized (this) {
            snapshot = new HashMap<>(entityCount);
        }

        if (snapshot.size() <= MAX_HISTORY_SIZE) {
            return snapshot;
        }

        Map<Integer, Integer> res = new HashMap<>();
        int stepSize = snapshot.size() / MAX_HISTORY_SIZE;
        List<Integer> availableTicks = snapshot.keySet().stream().sorted().collect(Collectors.toList());
        for(int i=0;i<snapshot.size();i+=stepSize){
            res.put(availableTicks.get(i), snapshot.get(availableTicks.get(i)));
        }

        return res;
    }

    public synchronized void addEntityCount(int count, int tick) {
        if (entityCount.isEmpty() || entityCount.get(this.latestTick) != count) {
            entityCount.put(tick,count);
            latestTick = tick;
        }
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public EntityCountHistory clone() {
        Map<Integer,Integer> entityCount;
        int latestTick;
        synchronized (this) {
            entityCount = new HashMap<>(this.entityCount);
            latestTick = this.latestTick;
        }
        return new EntityCountHistory(entityCount, latestTick);
    }
}
