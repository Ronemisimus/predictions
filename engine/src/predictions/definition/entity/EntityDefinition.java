package predictions.definition.entity;

import predictions.definition.property.api.PropertyDefinition;
import predictions.execution.instance.entity.EntityInstance;

import java.util.List;

public interface EntityDefinition {
    String getName();
    int getPopulation();
    List<PropertyDefinition<?>> getProps();
    boolean isInstance(EntityInstance entityInstance);
}
