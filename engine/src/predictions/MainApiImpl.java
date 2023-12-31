package predictions;

import dto.*;
import dto.subdto.SingleRunHistoryDto;
import dto.subdto.show.EntityListDto;
import dto.subdto.show.interactive.RunProgressDto;
import dto.subdto.show.world.EntityDto;
import dto.subdto.show.world.WorldDto;
import org.xml.sax.SAXException;
import predictions.client.container.ClientDataContainer;
import predictions.client.container.ClientDataContainerImpl;
import predictions.concurent.SimulationManager;
import predictions.concurent.SimulationManagerImpl;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.world.api.World;
import predictions.definition.world.impl.WorldImpl;
import predictions.execution.EntityCountHistory;
import predictions.execution.instance.world.WorldInstance;
import predictions.execution.instance.world.WorldInstanceImpl;
import predictions.generated.PRDWorld;
import predictions.termination.api.TerminationType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.StringReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class MainApiImpl implements MainApi {

    private final Map<String,World> possibleWorlds;
    private Map<String,ClientDataContainer> clientDataContainer;

    private final SimulationManager simulationManager;

    public MainApiImpl()
    {
        possibleWorlds = new HashMap<>();
        simulationManager = SimulationManagerImpl.getInstance();
        simulationManager.initializeThreadPool(1);
        clientDataContainer = new HashMap<>();
    }

    @Override
    public ReadFileDto readFile(String content) {
        PRDWorld res;

        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            Schema schema = schemaFactory.newSchema(getClass().getResource("predictions-v3.xsd"));
            JAXBContext context = JAXBContext.newInstance(PRDWorld.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setSchema(schema);
            res = (PRDWorld) unmarshaller.unmarshal(new StringReader(content));
        } catch (JAXBException | SAXException e) {
            return new ReadFileDto.Builder().matchesSchema(false).build();
        }

        ReadFileDto.Builder builder = new ReadFileDto.Builder().matchesSchema(true);
        try {
            World loaded = WorldImpl.fromPRD(res, builder);
            possibleWorlds.put(loaded.getName(), loaded);
            builder.name(loaded.getName());
        } catch (Exception e)
        {
            return builder.build();
        }

        return builder.fileLoaded().build();
    }

    @Override
    public ShowWorldDto showLoadedWorld(String name) {
        World selected = possibleWorlds.getOrDefault(name, null);
        if (selected == null) return new ShowWorldDto(null);
        WorldDto res = selected.getDto();
        return new ShowWorldDto(res);
    }

    @Override
    public EnvDto getEnv(String username) {
        return new EnvDto(clientDataContainer.get(username).getEnv());
    }

    public Integer runSimulation(String username) {
        ClientDataContainer cdc = clientDataContainer.get(username);
        World selected = possibleWorlds.get(cdc.getWorld());
        if (selected == null) return null;
        WorldInstance activeWorld = new WorldInstanceImpl(selected, (ClientDataContainerImpl) cdc);
        simulationManager.addSimulation(activeWorld, username);
        return activeWorld.getRunIdentifiers().getKey();
    }

    @Override
    public RunHistoryDto getRunHistory() {
        return simulationManager.getRunHistory();
    }

    @Override
    public SingleRunHistoryDto getRunEntityCounts(int runId) {
        Map<String, EntityCountHistory> res = simulationManager.getEntityCountHistory(runId);
        int tick = simulationManager.getSimulationTick(runId);
        return createEntityCountHistoryDto(res, tick);
    }

    private SingleRunHistoryDto createEntityCountHistoryDto(Map<String, EntityCountHistory> res, int tick) {
        List<String> entities = new ArrayList<>(res.keySet());
        List<Map<Integer, Integer>> counts = entities.stream()
                .map(res::get)
                .map(EntityCountHistory::getEntityCount)
                .collect(Collectors.toList());
        return new SingleRunHistoryDto(entities, counts, tick,null, null, null);
    }

    @Override
    public EntityListDto getEntityList(int runId) {
        return new EntityListDto(simulationManager.getEntityList(runId).stream()
                .map(EntityDefinition::getDto)
                .collect(Collectors.toList()));
    }

    @Override
    public SingleRunHistoryDto getRunPropertyHistogram(int runId, String entityName, String propertyName) {
        Map<Comparable<?>, Integer> propertyHist = simulationManager.getEntityPropertyHistogram(runId,entityName,propertyName);
        Double consistency = simulationManager.getConsistency(runId,entityName, propertyName);
        Double average = simulationManager.getAverage(runId,entityName, propertyName);
        return new SingleRunHistoryDto(null, null, null,propertyHist, consistency, average);
    }

    @Override
    public void setEnv(String username, String name, Comparable<?> value) {
        clientDataContainer.get(username).setEnv(name, value);
    }

    @Override
    public List<EntityDto> getEntityDefinitionCounts(String username) {
        return clientDataContainer.get(username).getEntityCounts();
    }

    @Override
    public void setEntityAmount(String username, String name, int count) {
        clientDataContainer.get(username).setEntityAmount(name, count);
    }

    @Override
    public void unload() throws InterruptedException {
        possibleWorlds.clear();
        clientDataContainer = null;
        simulationManager.unload();
    }

    @Override
    public RunProgressDto getRunProgress(Integer identifier) {
        return simulationManager.getRunProgress(identifier);
    }

    @Override
    public boolean stopSimulation(Integer identifier) {
        AtomicBoolean userCanTerminate = new AtomicBoolean(false);
        simulationManager.getTerminations(identifier)
                .forEachRemaining(t ->
                        userCanTerminate.set(
                                userCanTerminate.get() || t.getTerminationType().equals(TerminationType.USER)
                        )
                );
        if (userCanTerminate.get()) simulationManager.stopWorld(identifier);
        return userCanTerminate.get();
    }

    @Override
    public void pauseSimulation(Integer identifier) {
        simulationManager.pauseWorld(identifier);
    }

    @Override
    public void resumeSimulation(Integer identifier) {
        simulationManager.resumeWorld(identifier);
    }

    @Override
    public void reRunSimulation(Integer identifier) {
        simulationManager.reRunWorld(identifier);
    }

    @Override
    public EntityListDto getCurrentEntityAmounts(Integer identifier) {
        return simulationManager.getCurrentEntityAmounts(identifier);
    }

    @Override
    public void copyEnvironment(String username, Integer identifier) {
        ClientDataContainerImpl res = simulationManager.getEnvironment(identifier);
        this.clientDataContainer.put(username, new ClientDataContainerImpl(res));
    }

    @Override
    public void setThreadCount(int threadCount) {
        simulationManager.setThreadCount(threadCount);
    }

    @Override
    public List<String> getLoadedWorlds() {
        return new ArrayList<>(possibleWorlds.keySet());
    }

    @Override
    public void setClientContainer(String username, String worldName) {
        if (worldName!=null) {
            clientDataContainer.put(username, new ClientDataContainerImpl(possibleWorlds.get(worldName)));
        }else{
            clientDataContainer.remove(username);
        }
    }

    @Override
    public void setTermination(String username, boolean userTermination, Integer ticksLimit, Integer secondsLimit) {
        clientDataContainer.get(username).setTermination(userTermination, ticksLimit, secondsLimit);
    }

    @Override
    public RunHistoryDto getRunHistoryPerUser(String username) {
        return simulationManager.getRunHistoryPerUser(username);
    }
}
