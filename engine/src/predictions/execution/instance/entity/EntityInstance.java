package predictions.execution.instance.entity;

import predictions.definition.entity.EntityDefinition;
import predictions.execution.instance.property.PropertyInstance;

public interface EntityInstance {
    int getId();
    PropertyInstance<?> getPropertyByName(String name);
    void addPropertyInstance(PropertyInstance<?> propertyInstance);

    String getEntityTypeName();
}
