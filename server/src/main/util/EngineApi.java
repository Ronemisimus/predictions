package main.util;

import dto.ReadFileDto;
import dto.RunHistoryDto;
import dto.ShowWorldDto;
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
}
