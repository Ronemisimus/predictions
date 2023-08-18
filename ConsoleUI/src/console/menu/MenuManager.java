package console.menu;

public interface MenuManager {
    void runOption(int option);
    boolean stayOpen();

    int getOption();

    boolean run(boolean exited);
}
