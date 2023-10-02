package gui;

public class ServerApi {
    public static volatile ServerApi instance = null;
    private ServerApi() {}

    public static ServerApi getInstance() {
        if (instance == null) {
            synchronized (ServerApi.class) {
                if (instance == null)
                    instance = new ServerApi();
            }
        }
        return instance;
    }

    public boolean LoadFile(String filePath) {
        return true;
    }

    public void SetThreadCount(int enteredInteger) {

    }
}
