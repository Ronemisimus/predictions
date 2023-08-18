package console.menu.menu;

import console.menu.MenuManager;
import console.menu.MenuManagerImpl;
import console.menu.option.*;

import java.util.ArrayList;
import java.util.List;

public class FullMenu extends MenuManagerImpl {
    private static MenuManager active;
    private static final List<Boolean> canCloseMenu;
    private static final List<MenuItem> options;
    static {
        options = new ArrayList<>();
        canCloseMenu = new ArrayList<>();
        options.add(new OpenFile());
        canCloseMenu.add(false);
        options.add(new ShowSimulationDetails());
        canCloseMenu.add(false);
        options.add(new RunSimulation());
        canCloseMenu.add(false);
        options.add(new ShowRunDetails());
        canCloseMenu.add(false);
        options.add(new ExitOption());
        canCloseMenu.add(true);
    }
    private FullMenu() {
        super(options, canCloseMenu);
    }
    public static MenuManager getMenuManager() {
        if(active == null) {
            active = new FullMenu();
        }
        return active;
    }
}
