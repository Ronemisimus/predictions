package gui.scene.management.worldNameItem;

public class WorldNameItem {
    private final String hyperlink;
    private final String name;
    public WorldNameItem(String name, String hyperlink) {
        this.hyperlink = hyperlink;
        this.name = name;
    }

    public String getHyperlink() {
        return hyperlink;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
