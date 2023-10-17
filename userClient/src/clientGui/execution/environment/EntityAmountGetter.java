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
        entityAmount = initEntityAmount(entity);

        Button update = initUpdateButton(entity);
        this.getChildren().addAll(entityName, entityAmount, update);
    }

    private Button initUpdateButton(EntityDto entity) {
        Button res = new Button("Update");

        res.setOnAction(e -> {
            try{
                ServerApi.getInstance().setEntityAmount(entity.getName(), Integer.parseInt(entityAmount.getText()));
                Alert success = new Alert(Alert.AlertType.INFORMATION, "Successfully updated " + entity.getName(), ButtonType.OK);
                success.showAndWait();
            }catch (Exception ex){
                PopUtility.openPopup(getScene().getWindow(), "please enter a positive whole number\n" +
                        "the sum of all entities must not exceed the grid size", Alert.AlertType.ERROR);
            }
        });
        return res;
    }

    private TextField initEntityAmount(EntityDto entity) {
        TextField res =  new TextField(entity.getAmount().toString());
        res.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*")) {
                return change;
            }
            return null;
        }));
        return res;
    }
}
