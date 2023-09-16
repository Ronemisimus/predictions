package predictions.execution.instance.environment.api;

import predictions.execution.instance.property.PropertyInstance;

public interface ActiveEnvironment {
    PropertyInstance<?> getProperty(String name);
    void addPropertyInstance(PropertyInstance<?> propertyInstance);
}
