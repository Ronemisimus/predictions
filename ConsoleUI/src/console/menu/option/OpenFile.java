package console.menu.option;

public class OpenFile implements MenuItem{
    private boolean atLeastOneFileLoaded;

    public OpenFile() {
        this.atLeastOneFileLoaded = false;
    }

    @Override
    public boolean run() {
        System.out.println("opening file...");
        // TODO: send file name to engine and get success or fail
        this.atLeastOneFileLoaded = true;
        return atLeastOneFileLoaded;
    }

    @Override
    public String toString() {
        return "Read world from xml file";
    }
}
