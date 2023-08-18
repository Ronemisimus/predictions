package console.menu.option;

public class ShowRunDetails implements MenuItem{
    @Override
    public boolean run() {
        return false;
    }

    @Override
    public String toString() {
        return "Show details of previous simulation runs (opens a menu to choose the run)";
    }
}
