package predictions.concurent;

import dto.RunHistoryDto;
import dto.subdto.show.EntityListDto;
import dto.subdto.show.interactive.RunProgressDto;
import predictions.client.container.ClientDataContainerImpl;
import predictions.definition.entity.EntityDefinition;
import predictions.execution.EntityCountHistory;
import predictions.execution.instance.world.WorldInstance;
import predictions.termination.api.Termination;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public interface SimulationManager {

    RunHistoryDto getRunHistory();

    void initializeThreadPool(Integer threadCount);

    void addSimulation(WorldInstance activeWorld, String username);

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

    RunProgressDto getRunProgress(Integer identifier);

    EntityListDto getCurrentEntityAmounts(Integer identifier);
    ClientDataContainerImpl getEnvironment(Integer identifier);

    Iterator<Termination> getTerminations(Integer identifier);

    void setThreadCount(int threadCount);

    RunHistoryDto getRunHistoryPerUser(String username);
}
