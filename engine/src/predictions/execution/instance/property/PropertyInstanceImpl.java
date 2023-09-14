package predictions.execution.instance.property;

import dto.subdto.show.instance.PropertyInstanceDto;
import predictions.definition.property.api.PropertyDefinition;

public class PropertyInstanceImpl<T> implements PropertyInstance<T> {

    private final PropertyDefinition<T> propertyDefinition;
    private Comparable<T> value;

    private int timeModification;

    public PropertyInstanceImpl(PropertyDefinition<T> propertyDefinition, Comparable<T> value) {
        this.propertyDefinition = propertyDefinition;
        if (propertyDefinition.isLegal(value)) {
            this.value = value;
            this.timeModification = 0;
        }
        else {
            throw new IllegalArgumentException("Illegal value for property " + propertyDefinition.getName() + ", value: " + value);
        }
    }

    @Override
    public PropertyDefinition<T> getPropertyDefinition() {
        return propertyDefinition;
    }

    @Override
    public Comparable<T> getValue() {
        return value;
    }

    @Override
    public void updateValue(Comparable<?> val, int timeModification) {
        if (propertyDefinition.isLegal(val)) {
            this.value = (Comparable<T>) val;
            this.timeModification = timeModification;
        }
        else
        {
            Double from=null ,to=null;
            if (val instanceof Integer && this.value instanceof Integer) {
                from = ((Integer) this.propertyDefinition.getDto().getFrom()).doubleValue();
                to = ((Integer) this.propertyDefinition.getDto().getTo()).doubleValue();
            }
            if (val instanceof Double && this.value instanceof Double) {
                from = (Double) this.propertyDefinition.getDto().getFrom();
                to = (Double) this.propertyDefinition.getDto().getTo();
            }
            if (from!=null)
            {
                if (from > (Integer) val) {
                    this.value = (Comparable<T>) from;
                    this.timeModification = timeModification;
                }
                if (to < (Integer) val) {
                    this.value = (Comparable<T>) to;
                    this.timeModification = timeModification;
                }
            }
        }
    }

    @Override
    public int getTimeModification() {
        return timeModification;
    }

    @Override
    public PropertyInstanceDto getDto() {
        return new PropertyInstanceDto(propertyDefinition.getDto(), value);
    }

}
