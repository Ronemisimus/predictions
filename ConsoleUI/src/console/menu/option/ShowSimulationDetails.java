package console.menu.option;

import console.EngineApi;
import console.dto.presenter.DTOPresenter;

public class ShowSimulationDetails implements MenuItem{

    @Override
    public boolean run() {
        DTOPresenter res = EngineApi.getInstance().showLoadedWorld();
        System.out.println(res);
        return true;
    }

    @Override
    public String toString() {
        return "Show simulation details loaded from file";
    }
}
