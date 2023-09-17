package predictions.execution;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EntityCountHistory implements Cloneable {
    private final List<Integer> entityCount;

    private static final int MAX_HISTORY_SIZE = 4000;

    private int latestTick;

    public EntityCountHistory() {
        this.entityCount = new ArrayList<>();
        this.latestTick = -1;
    }

    private EntityCountHistory(List<Integer> entityCount, int latestTick) {
        this.entityCount = new ArrayList<>(entityCount);
        this.latestTick = latestTick;
    }

    public Map<Integer, Integer> getEntityCount() {
        List<Integer> snapshot;
        synchronized (this) {
            snapshot = new ArrayList<>(entityCount);
        }

        if (snapshot.size() <= MAX_HISTORY_SIZE) {
            return IntStream.range(0, snapshot.size())
                    .boxed()
                    .collect(Collectors.toMap(i -> i, snapshot::get, (a, b) -> b));
        }

        Map<Integer, Integer> res = new HashMap<>();
        int stepSize = snapshot.size() / MAX_HISTORY_SIZE;
        for (int i = 0; i < snapshot.size(); i += stepSize) {
            res.put(i, snapshot.get(i));
        }
        return res;
    }

    public synchronized void addEntityCount(int count, int tick) {
        if (entityCount.isEmpty() || entityCount.get(entityCount.size() - 1) != count || tick > latestTick + 1000) {
            entityCount.add(count);
            latestTick = tick;
        }
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public EntityCountHistory clone() {
        List<Integer> entityCount;
        int latestTick;
        synchronized (this) {
            entityCount = new ArrayList<>(this.entityCount);
            latestTick = this.latestTick;
        }
        return new EntityCountHistory(new ArrayList<>(entityCount), latestTick);
    }
}
