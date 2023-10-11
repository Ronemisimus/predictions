package clientGui.scene.details.tree;

import dto.ShowWorldDto;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;


public class WorldDetailsItem extends TreeItem<String> implements OpenableItem {
    private final ShowWorldDto data;

    public WorldDetailsItem(ShowWorldDto data) {
        super("world", null);
        this.data = data;
        TreeItem<String> env = new EnvItem(data.getWorld().getEnvironment());
        TreeItem<String> entities = new EntitiesItem(data.getWorld().getEntities());
        TreeItem<String> rules = new RulesItem(data.getWorld().getRules());
        TreeItem<String> terminations = new TerminationsItem(data.getWorld().getTicksTermination(), data.getWorld().getTimeTermination(), data.getWorld().isUserTermination());
        setExpanded(true);
        //noinspection unchecked
        this.getChildren().addAll(env, entities, rules, terminations);
    }

    @Override
    public Parent getDetailsView() {
        VBox parent = new VBox();
        Label grid = new Label("Grid: width of " + data.getWorld().getGridWidth() + " height of " + data.getWorld().getGridHeight());
        parent.getChildren().addAll(grid);
        return parent;
    }
}
