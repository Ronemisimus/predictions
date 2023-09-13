package predictions.definition.property.api;

import dto.subdto.show.world.PropertyDto;
import predictions.definition.value.generator.api.ValueGenerator;
import predictions.definition.value.generator.api.ValueGeneratorFactory;

import java.util.Objects;

public abstract class AbstractPropertyDefinition<T> implements PropertyDefinition<T> {

    private final String name;
    private final PropertyType propertyType;
    private ValueGenerator<T> valueGenerator;

    public AbstractPropertyDefinition(String name, PropertyType propertyType, ValueGenerator<T> valueGenerator) {
        this.name = name;
        this.propertyType = propertyType;
        this.valueGenerator = valueGenerator;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PropertyType getType() {
        return propertyType;
    }

    @Override
    public Comparable<T> generateValue() {
        return valueGenerator.generateValue();
    }

    protected ValueGenerator<T> getValueGenerator() {
        return valueGenerator;
    }

    @Override
    public boolean isLegal(Comparable<?> value) {
        try{
            propertyType.convert(value);
            return true;
        }catch (IllegalArgumentException e)
        {
            return false;
        }
    }

    @Override
    public abstract PropertyDto getDto();

    @Override
    public void setInit(Comparable<T> comparable) {
        valueGenerator = ValueGeneratorFactory.createFixed(comparable);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractPropertyDefinition<?> that = (AbstractPropertyDefinition<?>) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
