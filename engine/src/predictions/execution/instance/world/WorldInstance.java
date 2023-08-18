package predictions.execution.instance.world;

import predictions.definition.entity.EntityDefinition;
import predictions.execution.instance.environment.api.ActiveEnvironment;
import predictions.execution.instance.property.PropertyInstance;
import predictions.termination.api.Termination;

import java.time.LocalDateTime;
import java.util.Map;


public interface WorldInstance {
    Map.Entry<Integer, Termination> run();
    boolean setEnvironmentVariable(String name, Comparable<?> value);
    ActiveEnvironment getEnvironmentVariables();
    LocalDateTime getStartTime();
    Map<String, Integer> getEntityCounts();

    Map<Comparable<?>, Integer> getEntityPropertyHistogram(String entityName, String property);

}
