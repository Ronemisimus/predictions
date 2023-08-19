package predictions.execution.instance.world;

import predictions.definition.entity.EntityDefinition;
import predictions.execution.EntityCountHistory;
import predictions.execution.instance.environment.api.ActiveEnvironment;
import predictions.termination.api.Termination;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


public interface WorldInstance {
    Map.Entry<Integer, Termination> run();
    boolean setEnvironmentVariable(String name, Comparable<?> value);
    ActiveEnvironment getEnvironmentVariables();
    LocalDateTime getStartTime();
    Map<String, EntityCountHistory> getEntityCounts();

    Map<Comparable<?>, Integer> getEntityPropertyHistogram(String entityName, String property);

    List<EntityDefinition> getEntityDefinitions();
}
