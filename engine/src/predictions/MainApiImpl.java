package predictions;

import dto.*;
import dto.subdto.InitializeDto;
import dto.subdto.SingleRunHistoryDto;
import dto.subdto.show.EntityListDto;
import dto.subdto.show.world.WorldDto;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.world.api.World;
import predictions.definition.world.impl.WorldImpl;
import predictions.exception.BadExpressionException;
import predictions.exception.RepeatNameException;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainApiImpl implements MainApi {

    private WorldInstance activeWorld;

    private Map<Integer, WorldInstance> history;
    private World activeDefinition;

    public MainApiImpl()
    {
        this.history = new HashMap<>();
    }

    @Override
    public ReadFileDto readFile(String file) {
        File f = new File(file);

        boolean absolutePathError = !f.isAbsolute();
        boolean fileDoesNotExist = !f.exists();
        boolean isNotFile = !f.isFile();
        boolean isNotXML = !f.getName().endsWith(".xml");

        if(absolutePathError ||
                fileDoesNotExist ||
                isNotFile||
                isNotXML)
        {
            return GeneralDtoBuilder.getReadFileDtoBasic(absolutePathError, fileDoesNotExist, isNotFile, isNotXML);
        }

        PRDWorld res = null;

        try {
            JAXBContext context = JAXBContext.newInstance(PRDWorld.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            res = (PRDWorld) unmarshaller.unmarshal(f);
        } catch (JAXBException e) {
            return GeneralDtoBuilder.getReadFileDtoJAXB();
        }

        try {
            activeDefinition = WorldImpl.fromPRD(res);
        } catch (RepeatNameException e) {
            return GeneralDtoBuilder.getReadFileDtoRepeatName(e.isEnvironmentVariable(), e.getVariableName(), e.getEntityName());
        }catch (RuntimeException e)
        {
            return GeneralDtoBuilder.getReadFileDtoException(e.getCause());
        }
        catch (Exception e)
        {
            return GeneralDtoBuilder.getReadFileDtoUnknown();
        }

        return GeneralDtoBuilder.getReadFileDtoSuccess();
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
        return GeneralDtoBuilder.buildSingleRunDtoEntity(res);
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
        return GeneralDtoBuilder.buildSingleRunDtoProperty(propertyHist);
    }

    @Override
    public DTO exit() {
        return null;
    }

    @Override
    public void setEnv(String name, Optional<Comparable<?>> value) {
        activeDefinition.getEnvVariablesManager().set(name, value);
    }
}
