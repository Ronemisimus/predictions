package console.menu.menu;

import console.menu.MenuManagerImpl;
import console.menu.option.MenuItem;

import java.util.List;

public class HistoryMenu extends MenuManagerImpl {
    public HistoryMenu(List<MenuItem> options, List<Boolean> canCloseMEnu) {
        super(options, canCloseMEnu);
    }
}
