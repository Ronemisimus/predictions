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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class EntityInstanceManagerImpl implements EntityInstanceManager {

    private int count;
    private final List<EntityInstance> instances;

    private final List<Property<Coordinate>> locations;

    private EntityInstance[][] grid;

    public EntityInstanceManagerImpl() {
        count = 0;
        instances = new ArrayList<>();
        locations = new ArrayList<>();
        grid = null;
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
                .forEach(loc -> {
                    loc.setValue(getStepLocation(loc.getValue()));
                });
    }

    private Coordinate getStepLocation(Coordinate value) {
        EntityInstance moving  = this.grid[value.getX()][value.getY()];
        Integer gridWidth = this.grid.length;
        Integer gridHeight = this.grid[0].length;
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
        location.addListener((observable, oldValue, newValue) -> {
            updateGrid(newEntityInstance, newValue);
        });
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
        grid[coordinate.getX()][coordinate.getY()] = newEntityInstance;
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
        for (EntityInstance entityInstance : instances) {
            if (entityInstance.getId() == id) {
                Coordinate location = entityInstance.getLocation();
                locations.remove(location);
                instances.remove(entityInstance);
                grid[location.getX()][location.getY()] = null;
                return;
            }
        }
    }
}
