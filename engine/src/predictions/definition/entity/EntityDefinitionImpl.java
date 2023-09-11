package predictions.definition.entity;

import dto.ReadFileDto;
import dto.subdto.read.dto.EntityErrorDto;
import dto.subdto.read.dto.RepeatPropertyDto;
import dto.subdto.show.world.EntityDto;
import dto.subdto.show.world.PropertyDto;
import predictions.definition.property.api.PropertyDefinition;
import predictions.execution.instance.entity.EntityInstance;
import predictions.generated.PRDEntity;
import predictions.generated.PRDProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static predictions.ConverterPRDEngine.getPropertyDefinitionFromPRDEntity;

public class EntityDefinitionImpl implements EntityDefinition {

    private final String name;
    private int population;
    private final List<PropertyDefinition<?>> properties;

    public EntityDefinitionImpl(String name, int population) {
        this.name = name;
        this.population = population;
        properties = new ArrayList<>();
    }

    public EntityDefinitionImpl(PRDEntity prdEntity, ReadFileDto.Builder builder) {
        this(prdEntity.getName(), 0);
        prdEntity.getPRDProperties().getPRDProperty().stream()
                .map((PRDProperty def) -> getPropertyDefinitionFromPRDEntity(def, builder))
                .forEach(propertyDefinition -> {
                    if(properties.contains(propertyDefinition))
                    {
                        builder.entityError(
                                new EntityErrorDto.Builder()
                                        .repeatPropertyError(
                                                new RepeatPropertyDto(propertyDefinition.getName(), false, prdEntity.getName())
                                        ).build()
                        );
                        throw new RuntimeException("Duplicate property: " + propertyDefinition.getName());
                    }
                    else properties.add(propertyDefinition);
                });
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
        return new EntityDto(props, name, population);
    }

    @Override
    public void setPopulation(Integer population) {
        if (population>=0)
            this.population = population;
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
