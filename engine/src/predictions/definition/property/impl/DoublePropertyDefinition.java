package predictions.definition.property.impl;

import dto.subdto.show.world.PropertyDto;
import predictions.definition.property.api.AbstractPropertyDefinition;
import predictions.definition.property.api.PropertyType;
import predictions.definition.value.generator.api.ValueGenerator;

public class DoublePropertyDefinition extends AbstractPropertyDefinition<Double> {

    private final Double from;
    private final Double to;
    public DoublePropertyDefinition(String name, ValueGenerator<Double> valueGenerator, Double from, Double to) {
        super(name, PropertyType.FLOAT, valueGenerator);
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean isLegal(Comparable<?> value) {
        if(super.isLegal(value))
        {
            Double val = (Double) value;
            return (from==null && to==null) ||
                    (from==null && val<to) ||
                    (to==null && val>=from) ||
                    (from!=null && val!=null && val>=from && val<=to);
        }
        return false;
    }

    @Override
    public PropertyDto getDto() {
        return new PropertyDto(getType().name(),getName(),from,to, getValueGenerator().isRandomInit(), getValueGenerator().getInitValue());
    }
}
