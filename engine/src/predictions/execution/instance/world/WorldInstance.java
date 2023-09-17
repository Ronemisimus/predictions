package predictions.execution.instance.world;

import predictions.concurent.SimulationState;
import predictions.definition.entity.EntityDefinition;
import predictions.execution.EntityCountHistory;
import predictions.execution.instance.environment.api.ActiveEnvironment;
import predictions.termination.api.Termination;

import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;


public interface WorldInstance extends Runnable {
    void run();
    boolean setEnvironmentVariable(String name, Comparable<?> value);
    ActiveEnvironment getEnvironmentVariables();
    LocalDateTime getStartTime();
    Map<String, EntityCountHistory> getEntityCounts();

    Map<Comparable<?>, Integer> getEntityPropertyHistogram(String entityName, String property);

    List<EntityDefinition> getEntityDefinitions();

    Double getConsistency(String entityName, String property);

    Double getAverage(String entityName, String property);

    SimulationState getSimulationState();

    void stopWorld();

    void pauseWorld();

    void resumeWorld();

    void rerunWorld();
    int getCurrentTick();
    Map.Entry<Integer, Termination> getRunIdentifiers();
}
