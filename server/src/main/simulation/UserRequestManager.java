package main.simulation;

import java.util.HashMap;
import java.util.Map;

public class UserRequestManager {
    private static final UserRequestManager INSTANCE = new UserRequestManager();

    private final Map<String, Integer> userRequests;

    private UserRequestManager(){
        userRequests = new HashMap<>();
    }

    public static UserRequestManager getInstance(){
        return INSTANCE;
    }

    public void addRequest(String username, int requestId){
        userRequests.put(username, requestId);
    }

    public Integer getRequestId(String username){
        return userRequests.getOrDefault(username, null);
    }

    public void removeRequest(String username){
        userRequests.remove(username);
    }
}
