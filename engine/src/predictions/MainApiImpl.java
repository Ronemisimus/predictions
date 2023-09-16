package predictions;

import dto.*;
import dto.subdto.SingleRunHistoryDto;
import dto.subdto.read.dto.FileSelectionDto;
import dto.subdto.show.EntityListDto;
import dto.subdto.show.world.EntityDto;
import dto.subdto.show.world.WorldDto;
import org.xml.sax.SAXException;
import predictions.client.container.ClientDataContainer;
import predictions.client.container.ClientDataContainerImpl;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.world.api.World;
import predictions.definition.world.impl.WorldImpl;
import predictions.execution.EntityCountHistory;
import predictions.execution.instance.world.WorldInstance;
import predictions.execution.instance.world.WorldInstanceImpl;
import predictions.generated.PRDWorld;
import predictions.termination.api.Termination;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class MainApiImpl implements MainApi {

    private WorldInstance activeWorld;

    private final Map<Integer, WorldInstance> history;
    private World activeDefinition;
    private ClientDataContainer clientDataContainer;

    public MainApiImpl()
    {
        this.history = new HashMap<>();
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
            Schema schema = schemaFactory.newSchema(new File(Objects.requireNonNull(getClass().getResource("predictions-v2.xsd")).toURI()));
            JAXBContext context = JAXBContext.newInstance(PRDWorld.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setSchema(schema);
            res = (PRDWorld) unmarshaller.unmarshal(f);
        } catch (JAXBException | SAXException | URISyntaxException e) {
            return new ReadFileDto.Builder().matchesSchema(false).build();
        }

        ReadFileDto.Builder builder = new ReadFileDto.Builder().matchesSchema(true);
        try {
            activeDefinition = WorldImpl.fromPRD(res, builder);
            clientDataContainer = new ClientDataContainerImpl(activeDefinition);
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

    public void initialize() {
        clientDataContainer.initialize(activeDefinition);
        activeWorld = new WorldInstanceImpl(activeDefinition);
    }

    public void runSimulation() {
        Map.Entry<Integer, Termination> res = activeWorld.run();
        history.put(res.getKey(), activeWorld);
        new RunSimulationDto(res.getKey(), res.getValue().getDto());
    }

    @Override
    public RunHistoryDto getRunHistory() {
        Map<Integer, LocalDateTime> runNames = new HashMap<>();
        history.forEach((k,v) -> runNames.put(k, v.getStartTime()));
        return new RunHistoryDto(runNames);
    }

    @Override
    public SingleRunHistoryDto getRunEntityCounts(int runId) {
        Map<String, EntityCountHistory> res = history.get(runId).getEntityCounts();
        return createEntityCountHistoryDto(res);
    }

    private SingleRunHistoryDto createEntityCountHistoryDto(Map<String, EntityCountHistory> res) {
        List<String> entities = new ArrayList<>(res.keySet());
        List<Map<Integer, Integer>> counts = entities.stream().map(res::get).map(EntityCountHistory::getEntityCount).collect(Collectors.toList());
        return new SingleRunHistoryDto(entities, counts, null, null, null);
    }

    @Override
    public EntityListDto getEntityList(int runId) {
        return new EntityListDto(history.get(runId)
                .getEntityDefinitions().stream()
                .map(EntityDefinition::getDto)
                .collect(Collectors.toList()));
    }

    @Override
    public SingleRunHistoryDto getRunPropertyHistogram(int runId, String entityName, String propertyName) {
        Map<Comparable<?>, Integer> propertyHist = history.get(runId).getEntityPropertyHistogram(entityName,propertyName);
        Double consistency = history.get(runId).getConsistency(entityName, propertyName);
        Double average = history.get(runId).getAverage(entityName, propertyName);
        return new SingleRunHistoryDto(null, null, propertyHist, consistency, average);
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
    public void unload() {
        activeDefinition = null;
        clientDataContainer = null;
        history.clear();
        activeWorld = null;
    }
}
