package predictions.execution.instance.entity.manager;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.property.api.PropertyDefinition;
import predictions.execution.EntityCountHistory;
import predictions.execution.grid.Coordinate;
import predictions.execution.instance.entity.EntityInstance;
import predictions.execution.instance.entity.EntityInstanceImpl;
import predictions.execution.instance.property.PropertyInstance;
import predictions.execution.instance.property.PropertyInstanceImpl;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class EntityInstanceManagerImpl implements EntityInstanceManager {

    private int count;

    private final Map<EntityInstance,Property<Coordinate>> instance_locations;

    private final Set<Integer> killed_ids;

    private final Map<Integer, Map.Entry<EntityDefinition, Map.Entry<EntityDefinition,Boolean>>> replaceMap;

    private EntityInstance[][] grid;

    private final Map<String, EntityCountHistory> entityCountHistoryMap;

    public EntityInstanceManagerImpl(List<String> entities) {
        count = 0;
        instance_locations = new HashMap<>();
        grid = null;
        killed_ids = new HashSet<>();
        replaceMap = new HashMap<>();
        entityCountHistoryMap = new HashMap<>();
        entities.forEach(entity -> entityCountHistoryMap.put(entity, new EntityCountHistory()));
    }

    @Override
    public void initializeGrid(int gridWidth, int gridHeight) {
        grid = new EntityInstance[gridWidth][gridHeight];
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                grid[x][y] = null;
            }
        }
    }

    @Override
    public void moveEntities() {
        instance_locations.forEach((k, v) -> v.setValue(getStepLocation(v.getValue())));
    }

    private Coordinate getStepLocation(Coordinate value) {
        int gridWidth = this.grid.length;
        int gridHeight = this.grid[0].length;
        List<Coordinate> possibleMoves = Stream.of(
                new Coordinate((value.getX() + 1)%gridWidth, value.getY()),
                new Coordinate((gridWidth + value.getX() - 1)%gridWidth, value.getY()),
                new Coordinate(value.getX(), (value.getY() + 1)%gridHeight),
                new Coordinate(value.getX(), (gridHeight + value.getY() - 1)%gridHeight))
                .filter(c -> grid[c.getX()][c.getY()] == null)
                .collect(Collectors.toList());
        if (possibleMoves.isEmpty()) possibleMoves.add(value);
        return possibleMoves.get(new Random().nextInt(possibleMoves.size()));
    }

    @Override
    public EntityInstance create(EntityDefinition entityDefinition) {
        if (count>=grid.length*grid[0].length) throw new RuntimeException("Grid is full");
        count++;
        Coordinate coordinate = getRandomCoordinate();
        Property<Coordinate> location = new SimpleObjectProperty<>(coordinate);
        EntityInstance newEntityInstance = new EntityInstanceImpl(entityDefinition, count, location);
        location.addListener((Observable, oldVal, newVal) -> updateGrid(newEntityInstance, oldVal,newVal));
        instance_locations.put(newEntityInstance, location);

        entityDefinition.getProps().forEach(prop -> {
            PropertyInstance<?> res;

            switch (prop.getType()) {
                case STRING:
                    //noinspection unchecked
                    res = new PropertyInstanceImpl<>((PropertyDefinition<String>) prop, (String) prop.generateValue());
                    break;
                case DECIMAL:
                    //noinspection unchecked
                    res = new PropertyInstanceImpl<>((PropertyDefinition<Integer>) prop, (Integer) prop.generateValue());
                    break;
                case FLOAT:
                    //noinspection unchecked
                    res = new PropertyInstanceImpl<>((PropertyDefinition<Double>) prop, (Double) prop.generateValue());
                    break;
                case BOOLEAN:
                    //noinspection unchecked
                    res = new PropertyInstanceImpl<>((PropertyDefinition<Boolean>) prop, (Boolean) prop.generateValue());
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported property type");
            }
            newEntityInstance.addPropertyInstance(res);
        });
        if (coordinate != null) {
            grid[coordinate.getX()][coordinate.getY()] = newEntityInstance;
        }
        return newEntityInstance;
    }

    private void updateGrid(EntityInstance newEntityInstance, Coordinate oldValue, Coordinate newValue) {
        this.grid[oldValue.getX()][oldValue.getY()] = null;
        if(newEntityInstance!=null && newValue!=null) {
            this.grid[newValue.getX()][newValue.getY()] = newEntityInstance;
            newEntityInstance.setLocation(newValue);
            instance_locations.get(newEntityInstance).setValue(newValue);
        }
    }

    public void  printGrid()
    {
        for (EntityInstance[] entityInstances : grid) {
            for (int y = 0; y < grid[0].length; y++) {
                System.out.print((entityInstances[y] != null ? entityInstances[y].getEntityTypeName().substring(0, 1) : "N") + "  ");
            }
            System.out.println();
        }
    }



    private Coordinate getRandomCoordinate() {
        // collect empty grid Coordinates
        List<Coordinate> emptyCoordinates = IntStream.range(0, grid.length).boxed()
                .flatMap(col->IntStream.range(0, grid[0].length)
                        .filter(row->grid[col][row]==null).boxed()
                        .map(row->new Coordinate(col, row))).collect(Collectors.toList());
        return emptyCoordinates.isEmpty()?null: emptyCoordinates.get((int) (Math.random() * emptyCoordinates.size()));
    }

    @Override
    public List<EntityInstance> getInstances() {
        return new ArrayList<>(instance_locations.keySet());
    }

    @Override
    public void killEntity(int id) {
        killed_ids.add(id);
    }

    @Override
    public void replaceEntity(int id, EntityDefinition kill, EntityDefinition create, Boolean derived) {
        if (replaceMap.getOrDefault(id,null)==null) {
            replaceMap.put(id, new AbstractMap.SimpleEntry<>(create, new AbstractMap.SimpleEntry<>(kill,derived)));
        }
    }

    @Override
    public void finishKills() {
        if (!killed_ids.isEmpty()) {
            List<EntityInstance> temp = new ArrayList<>(getInstances());
            temp.stream()
                    .filter(e -> killed_ids.stream()
                            .anyMatch(id -> e.getId() == id))
                    .forEach(e -> {
                        Coordinate location = e.getLocation();
                        instance_locations.remove(e);
                        grid[location.getX()][location.getY()] = null;
                        count--;
                        e.setLocation(null);
                    });
            killed_ids.clear();
        }
    }

    @Override
    public void finishReplace(int tick) {
        if (!replaceMap.isEmpty()) {

            List<EntityInstance> temp = new ArrayList<>(getInstances());
            temp.stream().filter(e -> replaceMap.getOrDefault(e.getId(), null) != null).forEach(e -> {
                Map.Entry<EntityDefinition, Map.Entry<EntityDefinition, Boolean>> res = replaceMap.getOrDefault(e.getId(), null);
                Coordinate location = e.getLocation();
                grid[location.getX()][location.getY()] = null;
                count--;
                EntityInstance created = create(res.getKey());
                if (res.getValue().getValue()) {
                    res.getValue().getKey().getProps().stream()
                            .filter(prop -> res.getKey().getProps().contains(prop))
                            .forEach(prop ->
                                    created.getPropertyByName(prop.getName())
                                            .updateValue(
                                                    e.getPropertyByName(prop.getName()).getValue(),
                                                    tick
                                            )
                            );
                    created.setLocation(location);
                }
                instance_locations.remove(e);
            });
            replaceMap.clear();
        }
    }

    @Override
    public Map<String, EntityCountHistory> getEntityCounts() {
        Map<String, EntityCountHistory> temp = new HashMap<>(entityCountHistoryMap);
        Map<String, EntityCountHistory> copy = new HashMap<>();
        temp.forEach((key, value) -> copy.put(key, value.clone()));
        return copy;
    }

    @Override
    public void updateEntityCounts() {
        entityCountHistoryMap.forEach((ent, countHistory) -> countHistory.addEntityCount((int)getInstances().stream()
                .filter(entity-> entity.getEntityTypeName().equalsIgnoreCase(ent.toLowerCase()))
                .count()));
    }
}
