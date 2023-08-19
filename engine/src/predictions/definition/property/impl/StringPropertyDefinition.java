package predictions.definition.property.impl;

import dto.subdto.show.world.PropertyDto;
import predictions.definition.property.api.AbstractPropertyDefinition;
import predictions.definition.property.api.PropertyType;
import predictions.definition.value.generator.api.ValueGenerator;

public class StringPropertyDefinition extends AbstractPropertyDefinition<String> {

    public StringPropertyDefinition(String name, ValueGenerator<String> valueGenerator) {
        super(name, PropertyType.STRING, valueGenerator);
    }

    @Override
    public PropertyDto getDto() {
        return new PropertyDto(getType().name(), getName(), null, null, getValueGenerator().isRandomInit());
    }
}