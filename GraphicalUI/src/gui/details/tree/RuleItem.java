package gui.details.tree;

import dto.subdto.show.world.RuleDto;
import gui.details.tree.action.ActionItem;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;

public class RuleItem extends TreeItem<String> implements OpenableItem {
    private RuleDto rule;
    public RuleItem(RuleDto rule) {
        super(rule.getName(), null);
        this.rule = rule;
        rule.getActions().forEach(action -> getChildren().add(new ActionItem(action)));
    }

    @Override
    public Parent getDetailsView() {
        VBox detailsBox = new VBox();
        Double probability = rule.getProbability();
        String ruleName = rule.getName();
        Integer ticks = rule.getTicks();

        // Create Labels to display the information
        Label nameLabel = new Label("Rule Name: " + ruleName);
        Label probabilityLabel = new Label("Probability: " + probability);
        Label ticksLabel = new Label("Ticks: " + ticks);

        // Add Labels to the VBox
        detailsBox.getChildren().addAll(nameLabel, probabilityLabel, ticksLabel);

        return detailsBox;
    }
}
