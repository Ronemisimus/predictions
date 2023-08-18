package predictions.definition.property.impl;

import predictions.definition.property.api.AbstractPropertyDefinition;
import predictions.definition.property.api.PropertyType;
import predictions.definition.value.generator.api.ValueGenerator;

public class IntegerPropertyDefinition extends AbstractPropertyDefinition<Integer> {

    private final Integer from;
    private final Integer to;

    public IntegerPropertyDefinition(String name, ValueGenerator<Integer> valueGenerator, Integer from, Integer to) {
        super(name, PropertyType.DECIMAL, valueGenerator);
        this.from = from;
        this.to = to;
    }

    public IntegerPropertyDefinition(String name, ValueGenerator<Integer> valueGenerator) {
        this(name, valueGenerator, null, null);
    }

    @Override
    public boolean isLegal(Comparable<?> value) {
        if(super.isLegal(value))
        {
            Integer val = (Integer) value;
            return (from==null && to==null) ||
                    (from==null && val<to) ||
                    (to==null && val>=from) ||
                    (from!=null && val!=null && val>=from && val<=to);
        }
        return false;
    }
}
