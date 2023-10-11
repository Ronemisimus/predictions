package predictions;

import dto.*;
import dto.subdto.SingleRunHistoryDto;
import dto.subdto.show.EntityListDto;
import dto.subdto.show.interactive.RunProgressDto;
import dto.subdto.show.world.EntityDto;

import java.util.List;

public interface MainApi {
    ReadFileDto readFile(String content);
    ShowWorldDto showLoadedWorld(String name);

    EnvDto getEnv();
    void runSimulation(String name);

    RunHistoryDto getRunHistory();

    SingleRunHistoryDto getRunEntityCounts(int runId);

    EntityListDto getEntityList(int runId);
    
    SingleRunHistoryDto getRunPropertyHistogram(int runId, String entityName, String propertyName);

    void setEnv(String name, Comparable<?> value);

    List<EntityDto> getEntityDefinitionCounts();

    void setEntityAmount(String name, int i);

    void unload() throws InterruptedException;

    RunProgressDto getRunProgress(Integer identifier);

    boolean stopSimulation(Integer identifier);

    void pauseSimulation(Integer identifier);

    void resumeSimulation(Integer identifier);

    void reRunSimulation(Integer identifier);

    EntityListDto getCurrentEntityAmounts(Integer identifier);

    void copyEnvironment(Integer identifier);

    void setThreadCount(int threadCount);

    List<String> getLoadedWorlds();
}
