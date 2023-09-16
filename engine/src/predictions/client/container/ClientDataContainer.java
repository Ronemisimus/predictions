package predictions.client.container;

import dto.subdto.show.world.EntityDto;
import dto.subdto.show.world.PropertyDto;
import predictions.definition.world.api.World;

import java.util.List;

public interface ClientDataContainer {

    void setEntityAmount(String name, int i);

    void setEnv(String name, Comparable<?> value);

    void initialize(World activeDefinition);

    List<PropertyDto> getEnv();

    List<EntityDto> getEntityCounts();
}
