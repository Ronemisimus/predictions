package predictions;

import dto.*;
import dto.subdto.InitializeDto;
import dto.subdto.SingleRunHistoryDto;
import dto.subdto.show.EntityListDto;

import java.util.Optional;

public interface MainApi {
    ReadFileDto readFile(String file);
    ShowWorldDto showLoadedWorld();

    EnvDto getEnv();
    InitializeDto initialize();
    RunSimulationDto runSimulation();

    RunHistoryDto getRunHistory();

    SingleRunHistoryDto getRunEntityCounts(int runId);

    EntityListDto getEntityList(int runId);
    
    SingleRunHistoryDto getRunPropertyHistogram(int runId, String entityName, String propertyName);

    void setEnv(String name, Optional<Comparable<?>> value);
}
