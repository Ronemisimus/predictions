package main.util;

import dto.ReadFileDto;
import predictions.MainApi;
import predictions.MainApiImpl;

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
}
