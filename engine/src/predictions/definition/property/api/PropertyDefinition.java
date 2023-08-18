package predictions.definition.property.api;

import predictions.execution.instance.property.PropertyInstance;
import predictions.execution.instance.property.PropertyInstanceImpl;

public interface PropertyDefinition<T> {
    String getName();
    PropertyType getType();
    Comparable<T> generateValue();

    boolean isLegal(Comparable<?> value);

    static PropertyInstance<?> instantiate(PropertyDefinition<?> def) {
        return new PropertyInstanceImpl<Object>((PropertyDefinition<Object>) def, (Comparable<Object>) def.generateValue());
    }
}