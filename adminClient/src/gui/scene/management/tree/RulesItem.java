package gui.scene.management.tree;

import dto.subdto.show.world.RuleDto;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;

import java.util.List;

public class RulesItem extends TreeItem<String> implements OpenableItem{

    private final Integer ruleNumber;
    public RulesItem(List<RuleDto> rules) {
        super("rules");
        ruleNumber = rules.size();
        rules.forEach(rule -> getChildren().add(new RuleItem(rule)));
    }

    @Override
    public Parent getDetailsView() {
        VBox detailsBox = new VBox();

        // Create Labels to display the information
        Label ruleNumberLabel = new Label("There are " + ruleNumber + " rules in the system");

        // Add Labels to the VBox
        detailsBox.getChildren().addAll(ruleNumberLabel);

        // Return the VBox as a Parent
        return detailsBox;
    }
}
