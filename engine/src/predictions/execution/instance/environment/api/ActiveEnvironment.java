package predictions.execution.instance.environment.api;

import predictions.execution.instance.property.PropertyInstance;

import java.util.Map;
import java.util.Set;

public interface ActiveEnvironment {
    PropertyInstance<?> getProperty(String name);
    void addPropertyInstance(PropertyInstance<?> propertyInstance);

    Set<String> getEnvVariableNames();
}
