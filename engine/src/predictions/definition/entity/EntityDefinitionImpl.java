package predictions.definition.entity;

import dto.subdto.show.world.EntityDto;
import dto.subdto.show.world.PropertyDto;
import predictions.ConverterPRDEngine;
import predictions.definition.property.api.PropertyDefinition;
import predictions.execution.instance.entity.EntityInstance;
import predictions.generated.PRDEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EntityDefinitionImpl implements EntityDefinition {

    private final String name;
    private int population;
    private final List<PropertyDefinition<?>> properties;

    public EntityDefinitionImpl(String name, int population) {
        this.name = name;
        this.population = population;
        properties = new ArrayList<>();
    }

    public EntityDefinitionImpl(PRDEntity prdEntity) {
        this(prdEntity.getName(), 0);
        prdEntity.getPRDProperties().getPRDProperty().stream()
                .map(ConverterPRDEngine::getPropertyDefinitionFromPRDEntity)
                .forEach(properties::add);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
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
    public EntityDto getDto() {
        List<PropertyDto> props = properties.stream().map(PropertyDefinition::getDto).collect(Collectors.toList());
        return new EntityDto(props, name);
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
