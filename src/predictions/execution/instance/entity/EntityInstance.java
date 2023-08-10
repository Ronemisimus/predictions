package predictions.execution.instance.entity;

import predictions.execution.instance.property.PropertyInstance;

public interface EntityInstance {
    int getId();
    PropertyInstance getPropertyByName(String name);
    void addPropertyInstance(PropertyInstance propertyInstance);
}
