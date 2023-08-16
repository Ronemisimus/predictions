package predictions.execution.instance.property;

import predictions.definition.property.api.PropertyDefinition;

public class PropertyInstanceImpl implements PropertyInstance {

    private PropertyDefinition propertyDefinition;
    private Object value;

    private int timeModification;

    public PropertyInstanceImpl(PropertyDefinition propertyDefinition, Object value) {
        this.propertyDefinition = propertyDefinition;
        this.value = value;
    }

    @Override
    public PropertyDefinition getPropertyDefinition() {
        return propertyDefinition;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void updateValue(Object val, int timeModification) {
        this.value = val;
    }

    @Override
    public int getTimeModification() {
        return timeModification;
    }

}
