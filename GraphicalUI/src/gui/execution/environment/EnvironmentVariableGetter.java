package gui.execution.environment;

import dto.subdto.show.world.PropertyDto;
import gui.EngineApi;
import gui.util.PopUtility;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;


public class EnvironmentVariableGetter extends FlowPane {
    public EnvironmentVariableGetter(PropertyDto envProperty) {
        Label propName = new Label(envProperty.getName());
        TextField propValue = new TextField(envProperty.getInitValue()==null? "-" :envProperty.getInitValue().toString());
        Button update = new Button("Update");
        update.setOnAction(e -> {
            try{
                EngineApi.getInstance().setEnvironmentVariable(envProperty.getName(), propValue.getText());
            }catch (Exception ex){
                String message = "please enter a value of type" + envProperty.getType() + "\n"
                        + "and between " + envProperty.getFrom() + " and " + envProperty.getTo();
                PopUtility.openPopup(getScene().getWindow(), message, Alert.AlertType.ERROR);
            }
        });
        getChildren().addAll(propName, propValue, update);
    }
}
