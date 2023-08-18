package predictions.definition.entity;

import predictions.definition.property.api.PropertyDefinition;
import predictions.execution.instance.entity.EntityInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EntityDefinitionImpl implements EntityDefinition {

    private final String name;
    private final int population;
    private final List<PropertyDefinition<?>> properties;

    public EntityDefinitionImpl(String name, int population) {
        this.name = name;
        this.population = population;
        properties = new ArrayList<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPopulation() {
        return population;
    }

    @Override
    public List<PropertyDefinition<?>> getProps() {
        return properties;
    }

    @Override
    public boolean isInstance(EntityInstance entityInstance) {
        return entityInstance.getEntityTypeName().equals(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityDefinitionImpl that = (EntityDefinitionImpl) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
