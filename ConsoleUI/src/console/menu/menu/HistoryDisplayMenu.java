package console.menu.menu;

import console.menu.MenuManagerImpl;
import console.menu.option.ExitOption;
import console.menu.option.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class HistoryDisplayMenu extends MenuManagerImpl {
    private static final List<MenuItem> options;
    private static final List<Boolean> canClose;

    private static Integer choice;

    static{
        options = new ArrayList<>();
        canClose = new ArrayList<>();
        options.add(new MenuItem() {
            @Override
            public boolean run() {
                choice = 1;
                return true;
            }

            @Override
            public String toString() {
                return "show entity amounts";
            }
        });
        canClose.add(true);
        options.add(new MenuItem() {
            @Override
            public boolean run() {
                choice = 2;
                return true;
            }

            @Override
            public String toString() {
                return "property histogram across entity instances";
            }
        });
        canClose.add(true);
    }

    public HistoryDisplayMenu() {
        super(options, canClose);


    }

    public Integer getChoice() {
        return choice;
    }
}
