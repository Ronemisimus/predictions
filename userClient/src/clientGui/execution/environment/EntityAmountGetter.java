package clientGui.execution.environment;

import clientGui.util.PopUtility;
import clientGui.util.ServerApi;
import dto.subdto.show.world.EntityDto;
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
                ServerApi.getInstance().setEntityAmount(entity.getName(), Integer.parseInt(entityAmount.getText()));
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
