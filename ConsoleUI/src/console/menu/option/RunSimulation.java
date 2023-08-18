package console.menu.option;

public class RunSimulation implements MenuItem{

    private boolean atLeastOneRun;

    public RunSimulation() {
        this.atLeastOneRun = false;
    }

    @Override
    public boolean run() {
        System.out.println("running simulation...");
        // TODO: run the simulation and get success or fail
        this.atLeastOneRun = true;
        return atLeastOneRun;
    }

    @Override
    public String toString() {
        return "Run simulation from loaded file";
    }
}
