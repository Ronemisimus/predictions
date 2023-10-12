package gui.scene.requests;

import gui.util.ServerApi;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

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
        List<String> worldNames = ServerApi.getInstance().getWorldNames();
        worldNameComboBox.getItems().clear();
        worldNameComboBox.getItems().addAll(worldNames);
    }
}
