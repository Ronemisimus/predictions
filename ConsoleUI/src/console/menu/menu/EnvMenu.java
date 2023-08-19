package console.menu.menu;

import console.menu.MenuManagerImpl;
import console.menu.option.MenuItem;

import java.util.List;

public class EnvMenu extends MenuManagerImpl {
    public EnvMenu(List<MenuItem> options, List<Boolean> canCloseMEnu) {
        super(options, canCloseMEnu);
    }
}
