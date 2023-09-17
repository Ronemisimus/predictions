package predictions.concurent;

import dto.RunHistoryDto;
import predictions.definition.entity.EntityDefinition;
import predictions.execution.EntityCountHistory;
import predictions.execution.instance.world.WorldInstance;

import java.util.Collection;
import java.util.Map;

public interface SimulationManager {

    RunHistoryDto getRunHistory();

    void initializeThreadPool(Integer threadCount);

    void addSimulation(WorldInstance activeWorld);

    Map<String, EntityCountHistory> getEntityCountHistory(int runId);

    Collection<EntityDefinition> getEntityList(int runId);

    Map<Comparable<?>, Integer> getEntityPropertyHistogram(int runId, String entityName, String propertyName);

    Double getConsistency(int runId, String entityName, String propertyName);

    Double getAverage(int runId, String entityName, String propertyName);

    void stopWorld(int runId);

    void pauseWorld(int runId);

    void resumeWorld(int runId);

    void reRunWorld(int runId);

    void unload() throws InterruptedException;

    void updateState(int runId, SimulationState simulationState);

    int getSimulationTick(int runId);
}
