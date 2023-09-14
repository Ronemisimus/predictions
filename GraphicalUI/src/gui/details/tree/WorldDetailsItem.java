package gui.details.tree;

import dto.ShowWorldDto;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;


public class WorldDetailsItem extends TreeItem<String> implements OpenableItem {
    private ShowWorldDto data;
    private TreeItem<String> env;
    private TreeItem<String> entities;
    private TreeItem<String> rules;
    private TreeItem<String> terminations;

    public WorldDetailsItem(ShowWorldDto data) {
        super("world", null);
        this.data = data;
        env = new EnvItem(data.getWorld().getEnvironment());
        entities = new EntitiesItem(data.getWorld().getEntities());
        rules = new RulesItem(data.getWorld().getRules());
        terminations = new TerminationsItem(data.getWorld().getTicksTermination(), data.getWorld().getTimeTermination(), data.getWorld().isUserTermination());
        setExpanded(true);
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
