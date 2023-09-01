package gui.details.tree;

import dto.subdto.show.world.RuleDto;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;

import java.util.List;

public class RulesItem extends TreeItem<String> implements OpenableItem{

    private Integer ruleNumber;
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

        // Customize the VBox's appearance or layout if needed
        detailsBox.setSpacing(10); // Set spacing between items
        detailsBox.setStyle("-fx-padding: 10px;"); // Add padding to the VBox

        // Return the VBox as a Parent
        return detailsBox;
    }
}
