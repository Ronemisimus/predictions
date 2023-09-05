package gui.execution.environment;

import dto.subdto.show.world.EntityDto;
import gui.EngineApi;
import gui.util.PopUtility;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

public class EntityAmountGetter extends FlowPane {
    private Button update;
    private Label entityName;
    private TextField entityAmount;
    public EntityAmountGetter(EntityDto entity) {
        entityName = new Label(entity.getName() + " Amount: ");
        entityAmount = new TextField(entity.getAmount().toString());
        update = new Button("Update");
        update.setOnAction(e -> {
            try{
                EngineApi.getInstance().setEntityAmount(entity.getName(), Integer.parseInt(entityAmount.getText()));
            }catch (Exception ex){
                PopUtility.openPopup(getScene().getWindow(), "please enter a positive whole number", Alert.AlertType.ERROR);
            }
        });
        this.getChildren().addAll(entityName, entityAmount, update);
    }
}
