package predictions.execution.instance.world;

import dto.subdto.show.EntityListDto;
import predictions.client.container.ClientDataContainerImpl;
import predictions.concurent.SimulationState;
import predictions.definition.entity.EntityDefinition;
import predictions.execution.EntityCountHistory;
import predictions.termination.api.Termination;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


public interface WorldInstance extends Runnable {
    void run();
    void setEnvironmentVariable(String name, Comparable<?> value);

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
    int getCurrentTick();
    Map.Entry<Integer, Termination> getRunIdentifiers();
    Integer getMaxTick();
    Duration getRunningTime();
    Duration getMaxTime();
    EntityListDto getCurrentEntityCounts();

    ClientDataContainerImpl getClientContainer();
}
