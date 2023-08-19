package console.menu.option;

import console.EngineApi;
import console.dto.presenter.DTOPresenter;
import console.menu.MenuManager;

public class ShowRunDetails implements MenuItem{
    @Override
    public boolean run() {
        DTOPresenter res = EngineApi.getInstance().showPreviousRuns();
        System.out.println(res);
        return true;
    }

    @Override
    public String toString() {
        return "Show details of previous simulation runs (opens a menu to choose the run)";
    }
}
