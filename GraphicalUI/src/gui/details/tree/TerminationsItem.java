package gui.details.tree;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;


public class TerminationsItem extends TreeItem<String> implements OpenableItem {

    private Integer ticksTermination;
    private Integer timeTermination;
    private boolean userTermination;
    public TerminationsItem(Integer ticksTermination,
                            Integer timeTermination,
                            boolean userTermination) {
        super("termination terms", null);
        this.ticksTermination = ticksTermination;
        this.timeTermination = timeTermination;
        this.userTermination = userTermination;
    }

    @Override
    public Parent getDetailsView() {
        VBox detailsBox = new VBox();

        // Create Labels to display the information
        Label ticksLabel = new Label("Termination after " + ticksTermination + " ticks");
        Label timeLabel = new Label("Termination after " + timeTermination + " seconds");
        Label userLabel = new Label("User Termination " + (userTermination? "enabled" : "disabled"));

        // Add Labels to the VBox
        detailsBox.getChildren().addAll(ticksLabel, timeLabel, userLabel);

        // Customize the VBox's appearance or layout if needed
        detailsBox.setSpacing(10); // Set spacing between items
        detailsBox.setStyle("-fx-padding: 10px;"); // Add padding to the VBox

        // Return the VBox as a Parent
        return detailsBox;
    }
}
