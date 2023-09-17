package predictions.concurent;

import dto.RunHistoryDto;
import predictions.definition.entity.EntityDefinition;
import predictions.execution.EntityCountHistory;
import predictions.execution.instance.world.WorldInstance;

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

    private final Map<Integer, Future<Void>> worldFutures;

    private static SimulationManagerImpl instance;

    private SimulationManagerImpl(){
        worlds = new HashMap<>();
        simulationStates = new HashMap<>();
        worldFutures = new HashMap<>();
    }

    public static SimulationManager getInstance() {
        if (instance == null) {  // First if
            synchronized (SimulationManagerImpl.class) {
                if (instance == null) {  // Second if
                    instance = new SimulationManagerImpl();
                }
            }
        }
        return instance;
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
            simulationStates.put(runId, SimulationState.STOPPED);
        }
    }

    @Override
    public synchronized void pauseWorld(int runId) {
        if (simulationStates.containsKey(runId)) {
            worlds.get(runId).pauseWorld();
            simulationStates.put(runId, SimulationState.PAUSED);
        }
    }

    @Override
    public synchronized void resumeWorld(int runId) {
        if (simulationStates.containsKey(runId)) {
            worlds.get(runId).resumeWorld();
            simulationStates.put(runId, SimulationState.READY);
        }
    }

    @Override
    public synchronized void reRunWorld(int runId) {
        if (simulationStates.containsKey(runId)) {
            worlds.get(runId).stopWorld();
            synchronized (worlds.get(runId)) {
                worlds.get(runId).rerunWorld();
                //noinspection unchecked
                Future<Void> runIdFuture = (Future<Void>) executorService.submit(worlds.get(runId));
                worldFutures.put(runId, runIdFuture);
            }
            simulationStates.put(runId, SimulationState.READY);
        }
    }

    @Override
    public synchronized void unload() throws InterruptedException {
        executorService.shutdownNow();
        //noinspection ResultOfMethodCallIgnored
        executorService.awaitTermination(1, TimeUnit.MINUTES);
    }

    @Override
    public synchronized void updateStopped(int runId) {
        simulationStates.put(runId, SimulationState.STOPPED);
    }

    @Override
    public int getSimulationTick(int runId) {
        return worlds.get(runId).getCurrentTick();
    }
}
