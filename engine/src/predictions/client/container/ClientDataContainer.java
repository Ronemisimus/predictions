package predictions.client.container;

import dto.subdto.show.world.EntityDto;
import dto.subdto.show.world.PropertyDto;
import predictions.execution.instance.world.WorldInstance;

import java.util.List;

public interface ClientDataContainer {

    void setEntityAmount(String name, int i);

    void setEnv(String name, Comparable<?> value);

    void initialize(WorldInstance activeWorld);

    List<PropertyDto> getEnv();

    List<EntityDto> getEntityCounts();

    String getWorld();
}
