package predictions.execution.instance.world;

import predictions.definition.entity.EntityDefinition;
import predictions.definition.world.api.World;
import predictions.execution.EntityCountHistory;
import predictions.execution.context.Context;
import predictions.execution.context.ContextImpl;
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

public class WorldInstanceImpl implements WorldInstance{

    private ActiveEnvironment activeEnvironment;
    private EntityInstanceManager entityInstanceManager;
    private World world;

    private int tick;
    private Instant startTime;

    public WorldInstanceImpl(World world) {
        this.world = world;
        this.activeEnvironment = world.getEnvVariablesManager().createActiveEnvironment();
        this.entityInstanceManager = new EntityInstanceManagerImpl();
        this.tick = 0;

        world.getEntityDefinitions()
                .forEachRemaining(
                        entityDefinition ->
                                this.entityInstanceManager.create(entityDefinition));
    }

    @Override
    public Map.Entry<Integer, Termination> run() {
        this.startTime = Instant.now();
        Termination resTermination = null;
        Signal s = new SignalImpl(false, tick, this.startTime);
        while((resTermination = isTerminated(s))==null)
        {
            List<EntityInstance> tempList = new ArrayList<>(entityInstanceManager.getInstances());
            tempList.forEach(entityInstance -> {
                world.getRules().forEachRemaining(rule -> {
                    if(rule.getActivation().isActive(tick))
                    {
                        Context context = new ContextImpl(entityInstance, entityInstanceManager, activeEnvironment, tick);
                        rule.getActionsToPerform().forEach(action -> {
                            action.invoke(context);
                        });
                    }
                });
            });
            this.tick++;
            s = new SignalImpl(false,this.tick, this.startTime);
            System.out.println("finished tick " + this.tick);
        }
        return new AbstractMap.SimpleEntry<>(this.hashCode(), resTermination);
    }

    private Termination isTerminated(Signal signal)
    {
        Termination res = null;
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
        Map<String, EntityCountHistory> res = new HashMap<>();
        world.getEntityDefinitions().forEachRemaining(entityDefinition -> {
            res.put(entityDefinition.getName(),
                    new EntityCountHistory(entityDefinition.getPopulation(), Math.toIntExact(
                            entityInstanceManager.getInstances()
                                    .stream().filter(entityDefinition::isInstance).count())));
        });
        return res;
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
