package predictions.concurent;

import dto.RunHistoryDto;
import dto.subdto.show.EntityListDto;
import dto.subdto.show.interactive.RunProgressDto;
import predictions.client.container.ClientDataContainerImpl;
import predictions.definition.entity.EntityDefinition;
import predictions.execution.EntityCountHistory;
import predictions.execution.instance.world.WorldInstance;
import predictions.execution.instance.world.WorldInstanceImpl;

import java.time.Duration;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class SimulationManagerImpl implements SimulationManager{

    private ExecutorService executorService;

    private final Map<Integer, WorldInstance> worlds;

    private final Map<Integer, SimulationState> simulationStates;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final Map<Integer, Future<Void>> worldFutures;

    private SimulationManagerImpl(){
        worlds = new HashMap<>();
        simulationStates = new HashMap<>();
        worldFutures = new HashMap<>();
    }

    private static final class InstanceHolder {
        static final SimulationManagerImpl instance = new SimulationManagerImpl();
    }

    public static SimulationManager getInstance() {
        return InstanceHolder.instance;
    }

    @Override
    public RunHistoryDto getRunHistory() {
        return new RunHistoryDto(
                worlds.entrySet().stream()
                        .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().getStartTime()))
                        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)),
                simulationStates.entrySet().stream()
                        .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().getDto()))
                        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
        );
    }

    @Override
    public void initializeThreadPool(Integer threadCount) {
        executorService = Executors.newFixedThreadPool(threadCount);
    }

    @Override
    public void addSimulation(WorldInstance activeWorld) {
        worlds.put(activeWorld.getRunIdentifiers().getKey(), activeWorld);
        simulationStates.put(activeWorld.getRunIdentifiers().getKey(), activeWorld.getSimulationState());
        //noinspection unchecked
        worldFutures.put(activeWorld.getRunIdentifiers().getKey(), (Future<Void>) executorService.submit(activeWorld));
    }

    @Override
    public Map<String, EntityCountHistory> getEntityCountHistory(int runId) {
        return worlds.get(runId).getEntityCounts();
    }

    @Override
    public Collection<EntityDefinition> getEntityList(int runId) {
        return worlds.get(runId).getEntityDefinitions();
    }

    @Override
    public Map<Comparable<?>, Integer> getEntityPropertyHistogram(int runId, String entityName, String propertyName) {
        return worlds.get(runId).getEntityPropertyHistogram(entityName, propertyName);
    }

    @Override
    public Double getConsistency(int runId, String entityName, String propertyName) {
        return worlds.get(runId).getConsistency(entityName, propertyName);
    }

    @Override
    public Double getAverage(int runId, String entityName, String propertyName) {
        return worlds.get(runId).getAverage(entityName, propertyName);
    }

    @Override
    public synchronized void stopWorld(int runId) {
        if (simulationStates.containsKey(runId)) {
            worlds.get(runId).stopWorld();
        }
    }

    @Override
    public synchronized void pauseWorld(int runId) {
        if (simulationStates.containsKey(runId)) {
            worlds.get(runId).pauseWorld();
        }
    }

    @Override
    public synchronized void resumeWorld(int runId) {
        if (simulationStates.containsKey(runId)) {
            worlds.get(runId).resumeWorld();
        }
    }

    @Override
    public synchronized void reRunWorld(int runId) {
        if (simulationStates.containsKey(runId)) {
            WorldInstance newRun;
            synchronized (worlds.get(runId)) {
                 newRun = new WorldInstanceImpl((WorldInstanceImpl) worlds.get(runId));
            }
            addSimulation(newRun);
        }
    }

    @Override
    public synchronized void unload() throws InterruptedException {
        worlds.values()
                .forEach(WorldInstance::stopWorld);
        worldFutures.values().forEach(future -> future.cancel(true));
        if (executorService!=null) {
            executorService.shutdownNow();
            //noinspection ResultOfMethodCallIgnored
            executorService.awaitTermination(5, TimeUnit.MILLISECONDS);
        }
        worlds.clear();
        simulationStates.clear();
        worldFutures.clear();
    }

    @Override
    public synchronized void updateState(int runId, SimulationState simulationState) {
        simulationStates.put(runId, simulationState);
    }

    @Override
    public int getSimulationTick(int runId) {
        return worlds.get(runId).getCurrentTick();
    }

    @Override
    public RunProgressDto getRunProgress(Integer identifier) {
        WorldInstance checked = worlds.get(identifier);
        Integer tick = checked.getCurrentTick();
        Integer tickMax = checked.getMaxTick();
        Duration duration = checked.getRunningTime();
        Duration maxDuration = checked.getMaxTime();
        String status = simulationStates.get(identifier).name();
        return new RunProgressDto(tick,tickMax, duration,maxDuration, status);
    }

    @Override
    public EntityListDto getCurrentEntityAmounts(Integer identifier) {
        return worlds.get(identifier).getCurrentEntityCounts();
    }

    @Override
    public synchronized ClientDataContainerImpl getEnvironment(Integer identifier) {
        return worlds.get(identifier).getClientContainer();
    }
}
