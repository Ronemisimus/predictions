package main.util;

import dto.EnvDto;
import dto.ReadFileDto;
import dto.RunHistoryDto;
import dto.ShowWorldDto;
import dto.subdto.show.world.EntityDto;
import main.requests.request.Request;
import main.requests.request.TerminationType;
import main.requests.requestManager.RequestManager;
import main.simulation.UserRequestManager;
import predictions.MainApi;
import predictions.MainApiImpl;

import java.util.List;

public class EngineApi {
    private final MainApi mainApi;

    private final static EngineApi INSTANCE = new EngineApi();

    private EngineApi(){
        this.mainApi = new MainApiImpl();
    }

    public static EngineApi getInstance(){
        return INSTANCE;
    }

    public ReadFileDto readFile(char[] content) {
        return mainApi.readFile(String.valueOf(content));
    }

    public void setThreadCount(int threadCount) {
        mainApi.setThreadCount(threadCount);
    }

    public ShowWorldDto showLoadedWorld(String worldName) {
        return mainApi.showLoadedWorld(worldName);
    }

    public RunHistoryDto getRunStates() {

        return mainApi.getRunHistory();
    }

    public List<String> getLoadedWorlds() {
        return mainApi.getLoadedWorlds();
    }

    public void setSimulation(String username, String worldName) {
        mainApi.setClientContainer(username, worldName);
    }

    public void clearSimulation(String username) {
        mainApi.setClientContainer(username, null);
    }

    public EnvDto getEnv(String username) {
        return mainApi.getEnv(username);
    }

    public void setEnvironmentVariable(String username, String name, Comparable<?> value) {
        mainApi.setEnv(username, name, value);
    }

    public List<EntityDto> getEntities(String username) {
        return mainApi.getEntityDefinitionCounts(username);
    }

    public void setEntityAmount(String username, String name, int amount) {
        mainApi.setEntityAmount(username, name, amount);
    }

    public void runSimulation(String username) {
        Integer requestId = UserRequestManager.getInstance().getRequestId(username);
        Request request = RequestManager.getInstance().getRequest(requestId);
        mainApi.setTermination(username,
                request.getTerminationTypes().contains(TerminationType.USER),
                request.getTickLimit(),
                request.getSecondsLimit());
        Integer id = mainApi.runSimulation(username);
        request.addRun(id);
    }
}
