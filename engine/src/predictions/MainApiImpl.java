package predictions;

import dto.*;
import dto.subdto.InitializeDto;
import dto.subdto.SingleRunHistoryDto;
import dto.subdto.read.dto.FileSelectionDto;
import dto.subdto.show.EntityListDto;
import dto.subdto.show.world.EntityDto;
import dto.subdto.show.world.WorldDto;
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
import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class MainApiImpl implements MainApi {

    private WorldInstance activeWorld;

    private final Map<Integer, WorldInstance> history;
    private World activeDefinition;

    public MainApiImpl()
    {
        this.history = new HashMap<>();
    }

    @Override
    public ReadFileDto readFile(String file) {
        File f = new File(file);

        ReadFileDto resDto = checkPath(file, f);
        if(resDto != null) return resDto;

        PRDWorld res;

        try {
            JAXBContext context = JAXBContext.newInstance(PRDWorld.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            res = (PRDWorld) unmarshaller.unmarshal(f);
        } catch (JAXBException e) {
            return new ReadFileDto.Builder().matchesSchema(false).build();
        }

        ReadFileDto.Builder builder = new ReadFileDto.Builder().matchesSchema(true);
        try {
            activeDefinition = WorldImpl.fromPRD(res, builder);
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
        return new EnvDto(activeDefinition.getDto().getEnvironment());
    }

    public InitializeDto initialize() {
        activeWorld = new WorldInstanceImpl(activeDefinition);
        return activeWorld.getEnvironmentVariables().getDto();
    }

    public RunSimulationDto runSimulation() {
        Map.Entry<Integer, Termination> res = activeWorld.run();
        history.put(res.getKey(), activeWorld);
        return new RunSimulationDto(res.getKey(), res.getValue().getDto());
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
        List<Integer> startCounts = entities.stream().map(res::get).map(EntityCountHistory::getInitialCount).collect(Collectors.toList());
        List<Integer> finalCounts = entities.stream().map(res::get).map(EntityCountHistory::getEndCount).collect(Collectors.toList());
        return new SingleRunHistoryDto(entities, startCounts, finalCounts, null);
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
        return new SingleRunHistoryDto(null, null, null, propertyHist);
    }

    @Override
    public void setEnv(String name, Optional<Comparable<?>> value) {
        activeDefinition.getEnvVariablesManager().set(name, value);
    }

    @Override
    public List<EntityDto> getEntityDefinitionCounts() {
        List<EntityDto> res = new ArrayList<>();
        activeDefinition.getEntityDefinitions()
                .forEachRemaining( e -> res.add(e.getDto()));
        return res;
    }

    @Override
    public void setEntityAmount(String name, int i) {
        Optional<EntityDefinition> res = activeDefinition.getEntityDefinitionByName(name);
        res.ifPresent(entityDefinition -> entityDefinition.setPopulation(i));
    }
}
