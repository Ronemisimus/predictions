package predictions.definition.environment.api;

import predictions.definition.property.api.PropertyDefinition;
import predictions.execution.instance.environment.api.ActiveEnvironment;

import java.util.Collection;

public interface EnvVariablesManager {
    void addEnvironmentVariable(PropertyDefinition<?> propertyDefinition);
    ActiveEnvironment createActiveEnvironment();
    Collection<PropertyDefinition<?>> getEnvVariables();
}
