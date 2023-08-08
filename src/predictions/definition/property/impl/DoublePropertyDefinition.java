package predictions.definition.property.impl;

import predictions.definition.property.api.AbstractPropertyDefinition;
import predictions.definition.property.api.PropertyType;
import predictions.definition.value.generator.api.ValueGenerator;

public class DoublePropertyDefinition extends AbstractPropertyDefinition<Double> {
    public DoublePropertyDefinition(String name, ValueGenerator<Double> valueGenerator) {
        super(name, PropertyType.FLOAT, valueGenerator);
    }
}
