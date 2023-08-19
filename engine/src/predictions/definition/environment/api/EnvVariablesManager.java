package predictions.definition.environment.api;

import predictions.definition.property.api.PropertyDefinition;
import predictions.execution.instance.environment.api.ActiveEnvironment;

import java.util.Collection;
import java.util.Optional;

public interface EnvVariablesManager {
    void addEnvironmentVariable(PropertyDefinition<?> propertyDefinition);
    ActiveEnvironment createActiveEnvironment();
    Collection<PropertyDefinition<?>> getEnvVariables();

    void set(String name, Optional<Comparable<?>> value);
}
