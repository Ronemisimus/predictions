package predictions.execution.instance.entity;

import predictions.execution.grid.Coordinate;
import predictions.execution.instance.property.PropertyInstance;

public interface EntityInstance {
    int getId();
    PropertyInstance<?> getPropertyByName(String name);
    void addPropertyInstance(PropertyInstance<?> propertyInstance);

    Coordinate getLocation();

    void setLocation(Coordinate location);

    String getEntityTypeName();
}
