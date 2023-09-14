package predictions.execution.instance.entity;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import predictions.definition.entity.EntityDefinition;
import predictions.execution.grid.Coordinate;
import predictions.execution.instance.property.PropertyInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EntityInstanceImpl implements EntityInstance {

    private final EntityDefinition entityDefinition;
    private final int id;
    private final Map<String, PropertyInstance<?>> properties;

    private Property<Coordinate> location;

    public EntityInstanceImpl(EntityDefinition entityDefinition, int id, Property<Coordinate> location) {
        this.entityDefinition = entityDefinition;
        this.id = id;
        properties = new HashMap<>();
        this.location = new SimpleObjectProperty<>(location.getValue());
        this.location.bindBidirectional(location);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public PropertyInstance<?> getPropertyByName(String name) {
        if (!properties.containsKey(name)) {
            throw new IllegalArgumentException("for entity of type " + entityDefinition.getName() + " has no property named " + name);
        }

        return properties.get(name);
    }

    @Override
    public void addPropertyInstance(PropertyInstance<?> propertyInstance) {
        properties.put(propertyInstance.getPropertyDefinition().getName(), propertyInstance);
    }

    @Override
    public Coordinate getLocation() {
        return this.location.getValue();
    }

    @Override
    public void setLocation(Coordinate location) {
        this.location.setValue(location);
    }

    @Override
    public String getEntityTypeName() {
        return entityDefinition.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityInstanceImpl that = (EntityInstanceImpl) o;
        return id == that.id && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, location);
    }
}
