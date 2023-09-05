package gui.execution.environment;

import dto.subdto.show.world.EntityDto;
import gui.EngineApi;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.Popup;

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
                Popup popup = new Popup();
                popup.setWidth(300);
                popup.setHeight(100);
                popup.getContent().addAll(new Label("please enter a whole number"));
            }
        });
        this.getChildren().addAll(entityName, entityAmount, update);
    }
}
