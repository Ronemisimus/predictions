package predictions.definition.property.impl;

import predictions.definition.property.api.AbstractPropertyDefinition;
import predictions.definition.property.api.PropertyType;
import predictions.definition.value.generator.api.ValueGenerator;

public class BooleanPropertyDefenition extends AbstractPropertyDefinition<Boolean> {
    public BooleanPropertyDefenition(String name, ValueGenerator<Boolean> valueGenerator) {
        super(name, PropertyType.BOOLEAN, valueGenerator);
    }
}
