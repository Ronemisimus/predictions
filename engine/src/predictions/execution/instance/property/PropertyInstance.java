package predictions.execution.instance.property;

import predictions.definition.property.api.PropertyDefinition;

public interface PropertyInstance<T> {
    PropertyDefinition<T> getPropertyDefinition();
    Comparable<T> getValue();
    void updateValue(Comparable<?> val, int timeModification);
    int getTimeModification();

}
