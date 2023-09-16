package gui.execution.environment;

import dto.subdto.show.world.EntityDto;
import gui.EngineApi;
import gui.util.PopUtility;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;

public class EntityAmountGetter extends FlowPane {
    private final TextField entityAmount;
    public EntityAmountGetter(EntityDto entity) {
        Label entityName = new Label(entity.getName() + " Amount: ");
        entityAmount = new TextField(entity.getAmount().toString());
        Button update = new Button("Update");
        update.setOnAction(e -> {
            try{
                EngineApi.getInstance().setEntityAmount(entity.getName(), Integer.parseInt(entityAmount.getText()));
                Alert success = new Alert(Alert.AlertType.INFORMATION, "Successfully updated " + entity.getName(), ButtonType.OK);
                success.showAndWait();
            }catch (Exception ex){
                PopUtility.openPopup(getScene().getWindow(), "please enter a positive whole number\n" +
                        "the sum of all entities must not exceed the grid size", Alert.AlertType.ERROR);
            }
        });
        this.getChildren().addAll(entityName, entityAmount, update);
    }
}
