package clientGui.execution.environment;

import clientGui.util.PopUtility;
import clientGui.util.ServerApi;
import dto.subdto.show.world.PropertyDto;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;


public class EnvironmentVariableGetter extends FlowPane {
    public EnvironmentVariableGetter(PropertyDto envProperty) {
        Label propName = new Label(envProperty.getName());
        TextField propValue = new TextField(envProperty.getInitValue()==null? "-" :envProperty.getInitValue().toString());
        Button update = new Button("Update");
        update.setOnAction(e -> {
            try{
                ServerApi.getInstance().setEnvironmentVariable(envProperty.getName(), propValue.getText());
                Alert success = new Alert(Alert.AlertType.INFORMATION, "Successfully updated " + envProperty.getName(), ButtonType.OK);
                success.showAndWait();
            }catch (Exception ex){
                String message = "please enter a value of type " + envProperty.getType() + "\n"
                        + "and between " + envProperty.getFrom() + " and " + envProperty.getTo();
                PopUtility.openPopup(getScene().getWindow(), message, Alert.AlertType.ERROR);
            }
        });
        getChildren().addAll(propName, propValue, update);
    }
}
