package console.menu.menu;

import console.menu.MenuManager;
import console.menu.MenuManagerImpl;
import console.menu.option.ExitOption;
import console.menu.option.MenuItem;
import console.menu.option.OpenFile;

import java.util.ArrayList;
import java.util.List;

public class MenuPreFile extends MenuManagerImpl {
    private static final List<MenuItem> options;
    private static final List<Boolean> canCloseMenu;
    private static MenuManager active;
    static {
        options = new ArrayList<>();
        canCloseMenu = new ArrayList<>();
        options.add(new OpenFile());
        canCloseMenu.add(true);
        options.add(new ExitOption());
        canCloseMenu.add(true);
    }
    private MenuPreFile() {
        super(options, canCloseMenu);
    }

    public static MenuManager getMenuManager() {
        if(active == null) {
            active = new MenuPreFile();
        }
        return active;
    }
}
