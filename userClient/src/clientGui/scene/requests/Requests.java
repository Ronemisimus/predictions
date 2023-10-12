package clientGui.scene.requests;

import clientGui.util.ServerApi;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
public class Requests {
    @FXML
    private ComboBox<String> worldNameComboBox;
    @FXML
    private TextField runAmountTextField;
    @FXML
    private CheckBox userTerminationCheckBox;
    @FXML
    private CheckBox ticksTerminationCheckBox;
    @FXML
    private TextField ticksTerminationTextField;
    @FXML
    private CheckBox secondsTerminationCheckBox;
    @FXML
    private TextField secondsTerminationTextField;
    @FXML
    private Button submitButton;

    @FXML
    private void initialize() {
        List<String> worldNames = ServerApi.getInstance().getLoadedWorlds();
        worldNameComboBox.getItems().clear();
        worldNameComboBox.getItems().addAll(worldNames);

        ticksTerminationTextField.disableProperty().bind(ticksTerminationCheckBox.selectedProperty().not());
        secondsTerminationTextField.disableProperty().bind(secondsTerminationCheckBox.selectedProperty().not());
        ticksTerminationCheckBox.selectedProperty().addListener((o, oldValue, newValue) -> {
            if (!newValue) ticksTerminationTextField.setText("");
        });
        secondsTerminationCheckBox.selectedProperty().addListener((o, oldValue, newValue) -> {
            if (!newValue) secondsTerminationTextField.setText("");
        });

        runAmountTextField.setTextFormatter(new TextFormatter<>(this::change));
        secondsTerminationTextField.setTextFormatter(new TextFormatter<>(this::change));
        ticksTerminationTextField.setTextFormatter(new TextFormatter<>(this::change));
        
        submitButton.setOnAction(this::submitRequest);
    }

    private void submitRequest(ActionEvent actionEvent) {
        String worldName = worldNameComboBox.getValue();
        Integer runAmount = runAmountTextField.getText().isEmpty()?null:Integer.parseInt(runAmountTextField.getText());
        Boolean userTermination = userTerminationCheckBox.isSelected();
        Integer ticksTermination = ticksTerminationCheckBox.isSelected()?
                (ticksTerminationTextField.getText().isEmpty()?null:
                        Integer.parseInt(ticksTerminationTextField.getText())):null;
        Integer secondsTermination = secondsTerminationCheckBox.isSelected()?
                (secondsTerminationTextField.getText().isEmpty()?null:
                        Integer.parseInt(secondsTerminationTextField.getText())):null;
        ServerApi.getInstance().submitRequest(worldName, runAmount, userTermination, ticksTermination, secondsTermination);
    }

    private TextFormatter.Change change(TextFormatter.Change change) {
        if (change.getControlNewText().matches("\\d*")) {
            return change;
        }
        return null;
    }
}
