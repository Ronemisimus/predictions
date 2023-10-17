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

    EnvDto getEnv(String username);
    void runSimulation(String name);

    RunHistoryDto getRunHistory();

    SingleRunHistoryDto getRunEntityCounts(int runId);

    EntityListDto getEntityList(int runId);
    
    SingleRunHistoryDto getRunPropertyHistogram(int runId, String entityName, String propertyName);

    void setEnv(String username, String name, Comparable<?> value);

    List<EntityDto> getEntityDefinitionCounts(String username);

    void setEntityAmount(String username, String name, int count);

    void unload() throws InterruptedException;

    RunProgressDto getRunProgress(Integer identifier);

    boolean stopSimulation(Integer identifier);

    void pauseSimulation(Integer identifier);

    void resumeSimulation(Integer identifier);

    void reRunSimulation(Integer identifier);

    EntityListDto getCurrentEntityAmounts(Integer identifier);

    void copyEnvironment(String username, Integer identifier);

    void setThreadCount(int threadCount);

    List<String> getLoadedWorlds();

    void setClientContainer(String username, String worldName);
}
