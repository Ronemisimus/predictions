package predictions.client.container;

import dto.subdto.show.world.EntityDto;
import dto.subdto.show.world.PropertyDto;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.property.api.PropertyDefinition;
import predictions.definition.world.api.World;

import java.util.*;
import java.util.stream.Collectors;

public class ClientDataContainerImpl implements ClientDataContainer {

    private Map<String,Comparable<?>> envValues;
    private Map<String, PropertyDefinition> propertyDefinitions;
    private Integer gridWidth, gridHeight;
    private Map<String, Integer> entityAmounts;

    private Map<String, EntityDefinition> entityDefinitions;

    public ClientDataContainerImpl(World world) {
        gridHeight = world.getGridHeight();
        gridWidth = world.getGridWidth();

        propertyDefinitions = new HashMap<>();
        envValues = new HashMap<>();
        this.entityDefinitions = new HashMap<>();
        this.entityAmounts = new HashMap<>();

        for (PropertyDefinition p : world.getEnvVariablesManager().getEnvVariables()){
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

    @Override
    public void setEntityAmount(String name, int i) {
        if (i < 0) throw new RuntimeException("Negative amount");
        if (entityAmounts.containsKey(name)){
            if (entityAmounts.values().stream()
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
    public void setEnv(String name, Optional<Comparable<?>> value) {
        PropertyDefinition p = propertyDefinitions.get(name);
        if (p != null && value.isPresent()) {
            if (p.isLegal(value.get()))
            {
                p.setInit(value.get());
            }
        }
    }

    @Override
    public void initialize(World activeDefinition) {
        for (String name : envValues.keySet()) {
            activeDefinition.getEnvVariablesManager().set(name, Optional.ofNullable(envValues.get(name)));
        }
        for(String name : entityAmounts.keySet()){
            if (activeDefinition.getEntityDefinitionByName(name).isPresent())
                activeDefinition.getEntityDefinitionByName(name).get().setPopulation(entityAmounts.get(name));
        }
    }

    @Override
    public List<PropertyDto> getEnv() {
        return propertyDefinitions.values().stream()
                .map(PropertyDefinition::getDto)
                .map(dto -> new PropertyDto(dto.getType(), dto.getName(), dto.getFrom(), dto.getTo(), dto.isRandomInit(), dto.getInitValue()))
                .collect(Collectors.toList());
    }

    @Override
    public List<EntityDto> getEntityCounts() {
        return entityDefinitions.values().stream()
                .map(EntityDefinition::getDto)
                .map(dto -> new EntityDto(dto.getProps(), dto.getName(), entityAmounts.get(dto.getName())))
                .collect(Collectors.toList());
    }
}
