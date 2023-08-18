package console.menu.option;

public class ExitOption implements MenuItem{
    @Override
    public boolean run() {
        System.out.println("goodbye :)");
        return true;
    }

    @Override
    public String toString() {
        return "Exit";
    }
}
