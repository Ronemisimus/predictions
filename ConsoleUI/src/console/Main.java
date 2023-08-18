package console;

import console.menu.menu.MenuPreFile;
import console.menu.menu.MenuPreRun;
import static console.menu.menu.FullMenu.getMenuManager;

public class Main {
    public static void main(String[] args) {
        boolean exitPreFile = MenuPreFile.getMenuManager().run(false);
        boolean exitPreRun = MenuPreRun.getMenuManager().run(exitPreFile);
        getMenuManager().run(exitPreRun);
    }
}
