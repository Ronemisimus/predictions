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
    private final List<EntityInstance> instances;

    public EntityInstanceManagerImpl() {
        count = 0;
        instances = new ArrayList<>();
    }

    @Override
    public EntityInstance create(EntityDefinition entityDefinition) {

        count++;
        EntityInstance newEntityInstance = new EntityInstanceImpl(entityDefinition, count);
        instances.add(newEntityInstance);

        entityDefinition.getProps().forEach(prop -> {
            PropertyInstance<?> res;

            switch (prop.getType()) {
                case STRING:
                    res = new PropertyInstanceImpl<>((PropertyDefinition<String>) prop, (String) prop.generateValue());
                    break;
                case DECIMAL:
                    res = new PropertyInstanceImpl<>((PropertyDefinition<Integer>) prop, (Integer) prop.generateValue());
                    break;
                case FLOAT:
                    res = new PropertyInstanceImpl<>((PropertyDefinition<Double>) prop, (Double) prop.generateValue());
                    break;
                case BOOLEAN:
                    res = new PropertyInstanceImpl<>((PropertyDefinition<Boolean>) prop, (Boolean) prop.generateValue());
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported property type");
            }
            newEntityInstance.addPropertyInstance(res);
        });
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
