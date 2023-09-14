package predictions.execution.instance.entity.manager;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.property.api.PropertyDefinition;
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
    private final List<EntityInstance> instances;

    private final List<Property<Coordinate>> locations;

    private final Set<Integer> killed_ids;

    private final Map<Integer, Map.Entry<EntityDefinition, Map.Entry<EntityDefinition,Boolean>>> replaceMap;

    private EntityInstance[][] grid;

    public EntityInstanceManagerImpl() {
        count = 0;
        instances = new ArrayList<>();
        locations = new ArrayList<>();
        grid = null;
        killed_ids = new HashSet<>();
        replaceMap = new HashMap<>();
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
        IntStream.range(0, instances.size())
                .mapToObj(locations::get)
                .forEach(loc -> loc.setValue(getStepLocation(loc.getValue())));
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
        possibleMoves.add(value);
        return possibleMoves.get(new Random().nextInt(possibleMoves.size()));
    }

    @Override
    public EntityInstance create(EntityDefinition entityDefinition) {
        if (count>=grid.length*grid[0].length) throw new RuntimeException("Grid is full");
        count++;
        Coordinate coordinate = getRandomCoordinate();
        Property<Coordinate> location = new SimpleObjectProperty<>(coordinate);
        locations.add(location);
        EntityInstance newEntityInstance = new EntityInstanceImpl(entityDefinition, count, location);
        location.addListener((observable, oldValue, newValue) -> updateGrid(newEntityInstance, newValue));
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
        if (coordinate != null) {
            grid[coordinate.getX()][coordinate.getY()] = newEntityInstance;
        }
        return newEntityInstance;
    }

    private void updateGrid(EntityInstance newEntityInstance, Coordinate newValue) {
        int index = this.instances.indexOf(newEntityInstance);
        Coordinate oldValue = this.locations.get(index).getValue();
        this.grid[oldValue.getX()][oldValue.getY()] = null;
        this.grid[newValue.getX()][newValue.getY()] = newEntityInstance;
        this.locations.get(index).setValue(newValue);
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
        return instances;
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
            List<EntityInstance> temp = new ArrayList<>(instances);
            temp.stream()
                    .filter(e -> killed_ids.stream()
                            .anyMatch(id -> e.getId() == id))
                    .forEach(e -> {
                        Coordinate location = e.getLocation();
                        int idx = instances.indexOf(e);
                        locations.remove(idx);
                        instances.remove(idx);
                        grid[location.getX()][location.getY()] = null;
                        count--;
                    });
            killed_ids.clear();
        }
    }

    @Override
    public void finishReplace(int tick) {
        if (!replaceMap.isEmpty()) {

            List<EntityInstance> temp = new ArrayList<>(instances);
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
                int idx = instances.indexOf(e);
                instances.remove(idx);
                locations.remove(idx);
            });
            replaceMap.clear();
        }
    }
}
