package main.login;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private final Map<String, String> usernames;

    private final static UserManager instance = new UserManager();
    private UserManager() {
        usernames = new HashMap<>();
    }

    public static synchronized UserManager getInstance() {
        return instance;
    }

    public synchronized boolean userExists(String username) {
        return usernames.containsKey(username);
    }

    public synchronized void addUser(String username, HttpSession session) {
        usernames.put(username, session.getId());
    }

    public synchronized boolean sameUser(String username, HttpSession session) {
        return usernames.get(username).equals(session.getId());
    }
}
