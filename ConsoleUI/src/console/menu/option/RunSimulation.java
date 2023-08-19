package console.menu.option;

import console.EngineApi;
import console.dto.presenter.DTOPresenter;

public class RunSimulation implements MenuItem{

    private boolean atLeastOneRun;

    public RunSimulation() {
        this.atLeastOneRun = false;
    }

    @Override
    public boolean run() {
        DTOPresenter simulation = EngineApi.getInstance().runSimulation();
        System.out.println(simulation);
        this.atLeastOneRun = true;
        return true;
    }

    @Override
    public String toString() {
        return "Run simulation from loaded file";
    }
}
