package predictions.execution.instance.world;

import predictions.action.api.Action;
import predictions.action.api.ActionType;
import predictions.client.container.ClientDataContainer;
import predictions.concurent.SimulationManagerImpl;
import predictions.concurent.SimulationState;
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
import predictions.termination.api.TerminationType;
import predictions.termination.impl.SignalImpl;
import predictions.termination.impl.TicksTermination;
import predictions.termination.impl.TimeTermination;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

public class WorldInstanceImpl implements WorldInstance{

    private final ActiveEnvironment activeEnvironment;
    private final EntityInstanceManager entityInstanceManager;
    private final World world;
    private final ClientDataContainer clientDataContainer;
    private Termination reason;
    private SimulationState state;
    private static final List<ActionType> finalPhaseTypes = Arrays.asList(
            ActionType.KILL,// obvious
            ActionType.CONDITION, // can contain kill or replace
            ActionType.REPLACE, // obvious
            ActionType.PROXIMITY // has sub actions - could be "kill" or "replace"
    );

    private int tick;
    private final Instant startTime;
    private Duration duration;

    public WorldInstanceImpl(World world, ClientDataContainer clientDataContainer) {
        this.world = world;
        this.clientDataContainer = clientDataContainer;
        this.activeEnvironment = world.getEnvVariablesManager().createActiveEnvironment();
        List<String> entities = new ArrayList<>();
        world.getEntityDefinitions().forEachRemaining(entityDefinition -> entities.add(entityDefinition.getName()));
        this.entityInstanceManager = new EntityInstanceManagerImpl(entities);
        this.tick = 0;
        this.startTime = Instant.now();
        this.reason = null;
        this.entityInstanceManager.initializeGrid(world.getGridWidth(), world.getGridHeight());
        duration = Duration.ZERO;

        world.getEntityDefinitions()
                .forEachRemaining(entityDefinition -> IntStream.range(0,
                        Math.min(
                                clientDataContainer.getEntityCounts().stream()
                                        .filter(e -> e.getName().equals(entityDefinition.getName()))
                                        .findFirst().orElseThrow(() -> new RuntimeException("missing entity amount: " + entityDefinition.getName()))
                                        .getAmount(),
                                world.getGridWidth() * world.getGridHeight()))
                        .mapToObj(i -> entityDefinition)
                        .forEach(entityInstanceManager::create));
        state = SimulationState.WAITING;
        clientDataContainer.initialize(this);
    }

    public WorldInstanceImpl(final WorldInstanceImpl worldInstance) {
        this(worldInstance.world, worldInstance.clientDataContainer);
    }

    @Override
    public void run() {
        synchronized (this) {
            checkPause();
            state = SimulationState.READY;
            SimulationManagerImpl.getInstance().updateState(this.hashCode(), state);
        }
        Termination resTermination;
        Signal s = new SignalImpl(checkStop(), tick, duration);
        while((resTermination = isTerminated(s))==null)
        {

            Instant tickStart = Instant.now();
            doTick();
            synchronized (this) {
                duration = duration.plus(Duration.between(tickStart, Instant.now()));
                checkPause();
                tickStart = Instant.now();
                s = new SignalImpl(checkStop(), this.tick, duration);
                duration = duration.plus(Duration.between(tickStart, Instant.now()));
            }

        }
        this.reason = resTermination;
        synchronized (this) {
            if (state != SimulationState.STOPPED) {
                state = SimulationState.FINISHED;
            }
            SimulationManagerImpl.getInstance().updateState(this.hashCode(), state);
        }
    }

    private synchronized boolean checkStop() {
        return state == SimulationState.STOPPED;
    }

    private synchronized void checkPause() {
        if (state == SimulationState.PAUSED) {
            try {
                SimulationManagerImpl.getInstance().updateState(this.hashCode(), state);
                wait();
                SimulationManagerImpl.getInstance().updateState(this.hashCode(), state);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void doTick() {
        entityInstanceManager.updateEntityCounts(tick);
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
        synchronized (this) {
            this.tick++;
        }
    }

    private Termination isTerminated(Signal signal) {
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
    public void setEnvironmentVariable(String name, Comparable<?> value) {
        if (tick ==0) {
            try {
                this.activeEnvironment.getProperty(name).updateValue(value, this.tick);
            }
            catch (Exception ignored) {}
        }
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
        double count = 0.0;
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
        double count = 0.0;
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
    public synchronized SimulationState getSimulationState() {
        return state;
    }

    @Override
    public synchronized void stopWorld() {
        if (state != SimulationState.FINISHED) {
            if (state == SimulationState.PAUSED) this.notifyAll();
            this.state = SimulationState.STOPPED;
        }
    }

    @Override
    public synchronized void pauseWorld() {
        if (state == SimulationState.READY || state == SimulationState.WAITING)
            this.state = SimulationState.PAUSED;
    }

    @Override
    public synchronized void resumeWorld() {
        if (state == SimulationState.PAUSED) {
            state = SimulationState.READY;
            this.notifyAll();
        }
    }

    @Override
    public synchronized int getCurrentTick() {
        return this.tick;
    }

    @Override
    public Map.Entry<Integer, Termination> getRunIdentifiers() {
        return new AbstractMap.SimpleEntry<>(this.hashCode(), this.reason);
    }

    @Override
    public Integer getMaxTick() {
        AtomicReference<Integer> res = new AtomicReference<>(null);
        world.getTerminations().forEachRemaining(t -> {
            if (t.getTerminationType().equals(TerminationType.TICKS))
            {
                res.set(((TicksTermination) t).getTicks());
            }
        });
        return res.get();
    }

    @Override
    public synchronized Duration getRunningTime() {
        return duration;
    }

    @Override
    public Duration getMaxTime() {
        AtomicReference<Duration> res = new AtomicReference<>(null);
        world.getTerminations().forEachRemaining(t ->{
            if (t.getTerminationType().equals(TerminationType.TIME))
            {
                res.set(((TimeTermination) t).getTerminationDuration());
            }
        });
        return res.get();
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
