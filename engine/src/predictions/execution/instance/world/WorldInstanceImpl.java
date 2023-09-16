package predictions.execution.instance.world;

import predictions.action.api.Action;
import predictions.action.api.ActionType;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.property.api.PropertyType;
import predictions.definition.world.api.World;
import predictions.execution.EntityCountHistory;
import predictions.execution.instance.entity.EntityInstance;
import predictions.execution.instance.entity.manager.EntityInstanceManager;
import predictions.execution.instance.entity.manager.EntityInstanceManagerImpl;
import predictions.execution.instance.environment.api.ActiveEnvironment;
import predictions.execution.instance.property.PropertyInstance;
import predictions.termination.api.Signal;
import predictions.termination.api.Termination;
import predictions.termination.impl.SignalImpl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

public class WorldInstanceImpl implements WorldInstance{

    private final ActiveEnvironment activeEnvironment;
    private final EntityInstanceManager entityInstanceManager;
    private final World world;
    private static final List<ActionType> finalPhaseTypes = Arrays.asList(
            ActionType.KILL,// obvious
            ActionType.CONDITION, // can contain kill or replace
            ActionType.REPLACE, // obvious
            ActionType.PROXIMITY // has sub actions - could be "kill" or "replace"
    );

    private int tick;
    private Instant startTime;

    public WorldInstanceImpl(World world) {
        this.world = world;
        this.activeEnvironment = world.getEnvVariablesManager().createActiveEnvironment();
        List<String> entities = new ArrayList<>();
        world.getEntityDefinitions().forEachRemaining(entityDefinition -> entities.add(entityDefinition.getName()));
        this.entityInstanceManager = new EntityInstanceManagerImpl(entities);
        this.tick = 0;

        this.entityInstanceManager.initializeGrid(world.getGridWidth(), world.getGridHeight());

        world.getEntityDefinitions()
                .forEachRemaining(entityDefinition -> IntStream.range(0,
                        Math.min(
                                entityDefinition.getPopulation(),
                                world.getGridWidth() * world.getGridHeight()))
                        .mapToObj(i -> entityDefinition)
                        .forEach(entityInstanceManager::create));
    }

    @Override
    public Map.Entry<Integer, Termination> run() {
        this.startTime = Instant.now();
        Termination resTermination;
        Signal s = new SignalImpl(false, tick, this.startTime);
        while((resTermination = isTerminated(s))==null)
        {
            entityInstanceManager.updateEntityCounts();
            // move entities
            entityInstanceManager.moveEntities();

            // get runnable actions
            List<Action> firstPhaseActions = new ArrayList<>();
            List<Action> lastPhaseActions = new ArrayList<>();
            world.getRules().forEachRemaining(rule -> {
                if(rule.getActivation().isActive(tick))
                    rule.getActionsToPerform()
                            .forEach(action -> {
                                if (finalPhaseTypes.contains(action.getActionType()))
                                {
                                    lastPhaseActions.add(action);
                                }
                                else
                                {
                                    firstPhaseActions.add(action);
                                }
                            });
            });

            // execute actions phase 1
            List<EntityInstance> tempList = new ArrayList<>(entityInstanceManager.getInstances());
            tempList.forEach(entityInstance -> firstPhaseActions.forEach(action -> action.getContextDefinition()
                    .getContextList(entityInstance,
                            tempList,
                            entityInstanceManager,
                            activeEnvironment,
                            tick)
                    .forEach(action::invoke)));
            // execute actions phase 2: kill or replace
            tempList.forEach(entityInstance -> lastPhaseActions.forEach(action -> action.getContextDefinition()
                    .getContextList(entityInstance,
                            tempList,
                            entityInstanceManager,
                            activeEnvironment,
                            tick)
                    .forEach(action::invoke)));

            entityInstanceManager.finishKills();
            entityInstanceManager.finishReplace(this.tick);

            System.out.println(this.tick);

            this.tick++;
            s = new SignalImpl(false,this.tick, this.startTime);
        }
        return new AbstractMap.SimpleEntry<>(this.hashCode(), resTermination);
    }

    private Termination isTerminated(Signal signal)
    {
        Termination res;
        Iterator<Termination> it = world.getTerminations();
        while (it.hasNext())
        {
            res = it.next();
            if (res.isTermination(signal))
            {
                return res;
            }
        }
        return null;
    }

    @Override
    public boolean setEnvironmentVariable(String name, Comparable<?> value) {
        if (tick ==0) {
            try {
                this.activeEnvironment.getProperty(name).updateValue(value, 0);
                return true;
            }
            catch (Exception ignored) {}
        }
        return false;
    }

    @Override
    public ActiveEnvironment getEnvironmentVariables() {
        return activeEnvironment;
    }

    @Override
    public LocalDateTime getStartTime() {
        return LocalDateTime.ofInstant(startTime, TimeZone.getDefault().toZoneId());
    }

    @Override
    public Map<String, EntityCountHistory> getEntityCounts() {
        return entityInstanceManager.getEntityCounts();
    }

    @Override
    public Map<Comparable<?>, Integer> getEntityPropertyHistogram(String entityName, String property) {
        Map<Comparable<?>, Integer> res = new HashMap<>();
        Optional<EntityDefinition> ent = world.getEntityDefinitionByName(entityName);
        if (!ent.isPresent()) return null;
        EntityDefinition entityDefinition = ent.get();
        entityInstanceManager.getInstances().stream().filter(entityDefinition::isInstance).forEach(entityInstance -> {
            Comparable<?> val = entityInstance.getPropertyByName(property).getValue();
            res.put(val, res.getOrDefault(val, 0) + 1);
        });
        return res;
    }

    @Override
    public List<EntityDefinition> getEntityDefinitions() {
        List<EntityDefinition> res = new ArrayList<>();
        world.getEntityDefinitions().forEachRemaining(res::add);
        return res;
    }

    @Override
    public Double getConsistency(String entityName, String property) {
        Double consistency = 0.0;
        Double count = 0.0;
        for (EntityInstance entityInstance : entityInstanceManager.getInstances())
        {
            if (entityInstance.getEntityTypeName().equals(entityName))
            {
                consistency += entityInstance.getPropertyByName(property).getConsistency(tick);
                count++;
            }
        }
        return count==0?0:consistency/count;
    }

    @Override
    public Double getAverage(String entityName, String property) {
        Double avg = 0.0;
        Double count = 0.0;
        for (EntityInstance entityInstance : entityInstanceManager.getInstances())
        {
            if (entityInstance.getEntityTypeName().equals(entityName))
            {
                count++;
                PropertyInstance<?> propertyInstance = entityInstance.getPropertyByName(property);
                if (propertyInstance.getPropertyDefinition().getType().equals(PropertyType.DECIMAL) ||
                        propertyInstance.getPropertyDefinition().getType().equals(PropertyType.FLOAT))
                {
                    Comparable<?> val = propertyInstance.getValue();
                    if (val instanceof Integer) avg += ((Integer) val).doubleValue();
                    else avg += ((Double) val);
                }
                else{
                    return null;
                }
            }
        }
        return count==0?0:avg/count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorldInstanceImpl that = (WorldInstanceImpl) o;
        return Objects.equals(world, that.world) && Objects.equals(startTime, that.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, startTime);
    }
}
