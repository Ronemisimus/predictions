package predictions.definition.environment.api;

import predictions.definition.property.api.PropertyDefinition;
import predictions.execution.instance.environment.api.ActiveEnvironment;

import java.util.Collection;

public interface EnvVariablesManager {
    ActiveEnvironment createActiveEnvironment();
    Collection<PropertyDefinition<?>> getEnvVariables();

    void set(String name, Comparable<?> value);
}
