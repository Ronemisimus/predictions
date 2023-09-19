package predictions;

import dto.*;
import dto.subdto.SingleRunHistoryDto;
import dto.subdto.read.dto.FileSelectionDto;
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
import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class MainApiImpl implements MainApi {

    //private final Map<Integer, WorldInstance> history;
    private World activeDefinition;
    private ClientDataContainer clientDataContainer;

    private final SimulationManager simulationManager;

    public MainApiImpl()
    {
        simulationManager = SimulationManagerImpl.getInstance();
        clientDataContainer = null;
    }

    @Override
    public ReadFileDto readFile(String file) {
        File f = new File(file);

        ReadFileDto resDto = checkPath(file, f);
        if(resDto != null) return resDto;

        PRDWorld res;

        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            Schema schema = schemaFactory.newSchema(getClass().getResource("predictions-v2.xsd"));
            JAXBContext context = JAXBContext.newInstance(PRDWorld.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setSchema(schema);
            res = (PRDWorld) unmarshaller.unmarshal(f);
        } catch (JAXBException | SAXException e) {
            return new ReadFileDto.Builder().matchesSchema(false).build();
        }

        ReadFileDto.Builder builder = new ReadFileDto.Builder().matchesSchema(true);
        try {
            activeDefinition = WorldImpl.fromPRD(res, builder);
            clientDataContainer = new ClientDataContainerImpl(activeDefinition);
            simulationManager.initializeThreadPool(activeDefinition.getThreadCount());
        } catch (Exception e)
        {
            return builder.build();
        }

        return builder.fileLoaded().build();
    }

    private ReadFileDto checkPath(String path, File f){
        FileSelectionDto.Builder builder = new FileSelectionDto.Builder(path);

        if(!f.isAbsolute()) return new ReadFileDto.Builder()
                .fileSelectionError(
                        builder.fullPathError().build()
                ).build();
        if (!f.exists()) return new ReadFileDto.Builder()
                .fileSelectionError(
                        builder.fileExists().build()
                ).build();
        if(!f.isFile())
        {
            return new ReadFileDto.Builder()
                    .fileSelectionError(
                            builder.isFile().build()
                    ).build();
        }
        if(!f.getName().endsWith(".xml")) return new ReadFileDto.Builder()
                .fileSelectionError(
                        builder.isXML().build()
                ).build();
        return null;
    }

    @Override
    public ShowWorldDto showLoadedWorld() {
        WorldDto res = activeDefinition.getDto();
        return new ShowWorldDto(res);
    }

    @Override
    public EnvDto getEnv() {
        return new EnvDto(clientDataContainer.getEnv());
    }

    public void runSimulation() {
        WorldInstance activeWorld = new WorldInstanceImpl(activeDefinition, clientDataContainer);
        simulationManager.addSimulation(activeWorld);
        clientDataContainer = new ClientDataContainerImpl((ClientDataContainerImpl) clientDataContainer);
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
    public void setEnv(String name, Comparable<?> value) {
        clientDataContainer.setEnv(name, value);
    }

    @Override
    public List<EntityDto> getEntityDefinitionCounts() {
        return clientDataContainer.getEntityCounts();
    }

    @Override
    public void setEntityAmount(String name, int i) {
        clientDataContainer.setEntityAmount(name, i);
    }

    @Override
    public void unload() throws InterruptedException {
        activeDefinition = null;
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
        activeDefinition.getTerminations().forEachRemaining(t -> userCanTerminate.set(userCanTerminate.get() || t.getTerminationType().equals(TerminationType.USER)));
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
}
