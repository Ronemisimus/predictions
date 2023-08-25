package console.menu.option;

public class ExitOption implements MenuItem{

    private final String message;
    public ExitOption(String message)
    {
        this.message = message;
    }

    public ExitOption()
    {
        this.message = "goodbye :)";
    }

    @Override
    public boolean run() {
        System.out.println(this.message);
        return true;
    }

    @Override
    public String toString() {
        return "Exit";
    }
}
