package predictions.client.container;

import dto.subdto.show.world.EntityDto;
import dto.subdto.show.world.PropertyDto;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.property.api.PropertyDefinition;
import predictions.definition.world.api.World;
import predictions.execution.instance.world.WorldInstance;

import java.util.*;
import java.util.stream.Collectors;

public class ClientDataContainerImpl implements ClientDataContainer {

    private final Map<String,Comparable<?>> envValues;
    private final Map<String, PropertyDefinition<?>> propertyDefinitions;
    private final Integer gridWidth;
    private final Integer gridHeight;
    private final Map<String, Integer> entityAmounts;

    private final Map<String, EntityDefinition> entityDefinitions;

    private boolean userTermination;
    private Integer ticksLimit;

    private Integer secondsLimit;

    private final String world;

    public ClientDataContainerImpl(World world) {
        this.world = world.getName();
        gridHeight = world.getGridHeight();
        gridWidth = world.getGridWidth();

        propertyDefinitions = new HashMap<>();
        envValues = new HashMap<>();
        this.entityDefinitions = new HashMap<>();
        this.entityAmounts = new HashMap<>();

        this.userTermination = false;
        this.ticksLimit = null;
        this.secondsLimit = null;

        for (PropertyDefinition<?> p : world.getEnvVariablesManager().getEnvVariables()){
            PropertyDto pDto = p.getDto();
            envValues.put(pDto.getName(), pDto.getInitValue());
            propertyDefinitions.put(pDto.getName(), p);
        }

        Iterator<EntityDefinition> entityDefinitions = world.getEntityDefinitions();
        while (entityDefinitions.hasNext()) {
            EntityDefinition e = entityDefinitions.next();
            entityAmounts.put(e.getName(), e.getPopulation());
            this.entityDefinitions.put(e.getName(), e);
        }
    }

    public ClientDataContainerImpl(ClientDataContainerImpl clientDataContainer) {
        world = clientDataContainer.world;
        gridHeight = clientDataContainer.gridHeight;
        gridWidth = clientDataContainer.gridWidth;
        envValues = new HashMap<>(clientDataContainer.envValues);
        propertyDefinitions = clientDataContainer.propertyDefinitions;
        entityAmounts = new HashMap<>(clientDataContainer.entityAmounts);
        entityDefinitions = clientDataContainer.entityDefinitions;
        userTermination = clientDataContainer.userTermination;
        ticksLimit = clientDataContainer.ticksLimit;
        secondsLimit = clientDataContainer.secondsLimit;
    }

    @Override
    public void setEntityAmount(String name, int i) {
        if (i < 0) throw new RuntimeException("Negative amount");
        if (entityAmounts.containsKey(name)){
            if (entityAmounts.keySet().stream()
                    .filter(e -> !e.equals(name))
                    .map(entityAmounts::get)
                .mapToInt(Integer::intValue).sum() + i <= gridWidth * gridHeight) {
                entityAmounts.put(name, i);
            }
            else {
                throw new RuntimeException("Amount of entities exceeds grid size");
            }
        }
        else {
            throw new RuntimeException("Invalid entity name");
        }
    }

    @Override
    public void setEnv(String name, Comparable<?> value) {
        PropertyDefinition<?> p = propertyDefinitions.get(name);
        if (p != null) {
            if (p.isLegal(value))
            {
                envValues.put(name, value);
            }
            else {
                throw new RuntimeException("Illegal value");
            }
        }
        else {
            throw new RuntimeException("Invalid environment variable name");
        }
    }

    @Override
    public void initialize(WorldInstance activeWorld) {
        for (String name : envValues.keySet()) {
            Optional<Comparable<?>> res = Optional.ofNullable(envValues.get(name));
            res.ifPresent(comparable -> activeWorld.setEnvironmentVariable(name, comparable));
        }

        activeWorld.setTerminations(userTermination, ticksLimit, secondsLimit);
    }

    @Override
    public List<PropertyDto> getEnv() {
        return propertyDefinitions.values().stream()
                .map(PropertyDefinition::getDto)
                .map(dto -> new PropertyDto(dto.getType(), dto.getName(), dto.getFrom(), dto.getTo(), dto.isRandomInit(), envValues.get(dto.getName())))
                .collect(Collectors.toList());
    }

    @Override
    public List<EntityDto> getEntityCounts() {
        return entityDefinitions.values().stream()
                .map(EntityDefinition::getDto)
                .map(dto -> new EntityDto(dto.getProps(), dto.getName(), entityAmounts.get(dto.getName())))
                .collect(Collectors.toList());
    }

    @Override
    public String getWorld() {
        return world;
    }

    public boolean isUserTermination() {
        return userTermination;
    }

    public Integer getTicksLimit() {
        return ticksLimit;
    }

    public Integer getSecondsLimit() {
        return secondsLimit;
    }

    @Override
    public void setTermination(boolean userTermination, Integer ticksLimit, Integer secondsLimit) {
        this.userTermination = userTermination;
        this.ticksLimit = ticksLimit;
        this.secondsLimit = secondsLimit;
    }
}
