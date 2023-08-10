package predictions.execution.instance.entity.manager;

import predictions.definition.entity.EntityDefinition;
import predictions.definition.property.api.PropertyDefinition;
import predictions.execution.instance.entity.EntityInstance;
import predictions.execution.instance.entity.EntityInstanceImpl;
import predictions.execution.instance.property.PropertyInstance;
import predictions.execution.instance.property.PropertyInstanceImpl;

import java.util.ArrayList;
import java.util.List;

public class EntityInstanceManagerImpl implements EntityInstanceManager {

    private int count;
    private List<EntityInstance> instances;

    public EntityInstanceManagerImpl() {
        count = 0;
        instances = new ArrayList<>();
    }

    @Override
    public EntityInstance create(EntityDefinition entityDefinition) {

        count++;
        EntityInstance newEntityInstance = new EntityInstanceImpl(entityDefinition, count);
        instances.add(newEntityInstance);

        for (PropertyDefinition propertyDefinition : entityDefinition.getProps()) {
            Object value = propertyDefinition.generateValue();
            PropertyInstance newPropertyInstance = new PropertyInstanceImpl(propertyDefinition, value);
            newEntityInstance.addPropertyInstance(newPropertyInstance);
        }

        return newEntityInstance;
    }

    @Override
    public List<EntityInstance> getInstances() {
        return instances;
    }

    @Override
    public void killEntity(int id) {
        for (EntityInstance entityInstance : instances) {
            if (entityInstance.getId() == id) {
                instances.remove(entityInstance);
                return;
            }
        }
    }
}
