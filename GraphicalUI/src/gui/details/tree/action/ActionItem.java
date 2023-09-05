package gui.details.tree.action;

import dto.subdto.show.world.action.*;
import gui.details.tree.OpenableItem;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;

public class ActionItem extends TreeItem<String> implements OpenableItem {
    private final ActionDto action;
    public ActionItem(ActionDto action) {
        super(action.getType(), null);
        this.action = action;
        addChildren();
    }

    private void addChildren() {
        switch (action.getType()) {
            case "Condition":
                if (action instanceof ConditionActionDto)
                    addChildren((ConditionActionDto) action);
            case "Proximity":
                if (action instanceof ProximityActionDto)
                    addChildren((ProximityActionDto) action);
        }
    }

    private void addChildren(ProximityActionDto action) {
        action.getActions().forEach(this::accept);
    }

    private void addChildren(ConditionActionDto action) {
        TreeItem<String> thenItem = new TreeItem<>("then");
        TreeItem<String> elseItem = new TreeItem<>("else");
        action.getThenActions().forEach(act -> thenItem.getChildren().add(new ActionItem(act)));
        action.getElseActions().forEach(act -> elseItem.getChildren().add(new ActionItem(act)));
        getChildren().add(thenItem);
        getChildren().add(elseItem);
    }

    @Override
    public Parent getDetailsView() {
        switch (action.getType().toLowerCase()) {
            case "increase":
                if (action instanceof IncreaseActionDto) return getDetailsView((IncreaseActionDto) action);
                else return null;
            case "decrease":
                if (action instanceof decreaseActionDto) return getDetailsView((decreaseActionDto) action);
                else return null;
            case "calculation":
                if (action instanceof CalculationActionDto) return getDetailsView((CalculationActionDto) action);
                else return null;
            case "kill":
                if (action instanceof KillActionDto) return getDetailsView((KillActionDto) action);
                else return null;
            case "set":
                if (action instanceof SetActionDto) return getDetailsView((SetActionDto) action);
                else return null;
            case "replace":
                if (action instanceof ReplaceActionDto) return getDetailsView((ReplaceActionDto) action);
                else return null;
            case "condition":
                if (action instanceof ConditionActionDto) return getDetailsView((ConditionActionDto) action);
                else return null;
            case "proximity":
                if (action instanceof ProximityActionDto) return getDetailsView((ProximityActionDto) action);
                else return null;
            default:
                return null;
        }
    }

    private Parent getDetailsView(ProximityActionDto action) {
        VBox detailsBox = new VBox();

        Label nameLabel = new Label("Type: Proximity");
        Label sourceEntityLabel = new Label("Source Entity: " + (action.getSourceEntity()==null? "" :
                action.getSourceEntity().getName()));
        Label targetEntityLabel = new Label("Target Entity: " + (action.getTargetEntity()==null? "" :
                action.getTargetEntity().getName()));
        Label distanceLabel = new Label("sub actions activated if source and target\n entities are within a distance of " + action.getOfValue());
        detailsBox.getChildren().addAll(nameLabel, sourceEntityLabel, targetEntityLabel, distanceLabel);

        return detailsBox;
    }

    private Parent getDetailsView(ConditionActionDto action) {
        Label nameLabel = new Label("Type: Condition");
        Label conditionLabel = new Label("then sub actions performed if:" + action.getConditionExpression());
        Label elseLabel = new Label("otherwise else sub actions performed");
        Label primaryEntityLabel = new Label("Primary Entity: " + action.getPrimaryEntity().getName());
        Label secondaryEntityLabel = new Label("Secondary Entity: " + (action.getSecondaryEntity()==null? "" :
                action.getSecondaryEntity().getName()));

        VBox detailsBox = new VBox();
        detailsBox.getChildren().addAll(nameLabel, primaryEntityLabel, secondaryEntityLabel, conditionLabel, elseLabel);

        return detailsBox;
    }

    private Parent getDetailsView(ReplaceActionDto action) {
        Label typeLabel = new Label("Type: Replace");
        Label bornEntity = new Label("Born Entity: " + (action.getBornEntity()==null? "" :
                action.getBornEntity().getName()));
        Label deadEntity = new Label("Killed Entity: " + (action.getKilledEntity()==null? "" :
                action.getKilledEntity().getName()));
        Label modeLabel = new Label("Mode: " + action.getMode());

        VBox detailsBox = new VBox();
        detailsBox.getChildren().addAll(typeLabel, bornEntity, deadEntity, modeLabel);

        return detailsBox;
    }

    private Parent getDetailsView(SetActionDto action) {
        Label typeLabel = new Label("Type: Set");
        Label entityLabel = new Label("Primary Entity: " + action.getPrimaryEntity().getName());
        Label secondaryEntityLabel = new Label("Secondary Entity: " + (action.getSecondaryEntity()==null? "" :
                action.getSecondaryEntity().getName()));
        Label propertyLabel = new Label("Property: " + action.getPropertyName() + " = " + action.getValueExpression());

        VBox detailsBox = new VBox();
        detailsBox.getChildren().addAll(typeLabel, entityLabel, secondaryEntityLabel, propertyLabel);

        return detailsBox;
    }

    private Parent getDetailsView(KillActionDto action) {
        Label typeLabel = new Label("Type: Kill");
        Label primaryEntityLabel = new Label("Killed Entity: " + action.getPrimaryEntity().getName());

        VBox detailsBox = new VBox();
        detailsBox.getChildren().addAll(typeLabel, primaryEntityLabel);

        return detailsBox;
    }

    private Parent getDetailsView(CalculationActionDto action) {
        Label typeLabel = new Label("Type: Calculation");
        Label primaryEntityLabel = new Label("Primary Entity: " + action.getPrimaryEntity().getName());
        Label secondaryEntityLabel = new Label("Secondary Entity: " + (action.getSecondaryEntity()==null? "" :
                action.getSecondaryEntity().getName()));
        Label propertyLabel = new Label("Property: " + action.getResultPropName() + " = " + action.getCalculationExpression());

        VBox detailsBox = new VBox();
        detailsBox.getChildren().addAll(typeLabel, primaryEntityLabel, secondaryEntityLabel, propertyLabel);

        return detailsBox;
    }

    private Parent getDetailsView(decreaseActionDto action) {
        Label typeLabel = new Label("Type: Decrease");
        Label primaryEntityLabel = new Label("Primary Entity: " + action.getPrimaryEntity().getName());
        Label secondaryEntityLabel = new Label("Secondary Entity: " + (action.getSecondaryEntity()==null? null:
                action.getSecondaryEntity().getName()));
        Label propertyLabel = new Label("Property: " + action.getPropertyName() + " = " + action.getPropertyName() + " - 1");

        VBox detailsBox = new VBox();
        detailsBox.getChildren().addAll(typeLabel, primaryEntityLabel, secondaryEntityLabel, propertyLabel);

        return detailsBox;
    }

    private Parent getDetailsView(IncreaseActionDto action) {
        Label typeLabel = new Label("Type: Increase");
        Label primaryEntityLabel = new Label("Primary Entity: " + action.getPrimaryEntity().getName());
        Label secondaryEntityLabel = new Label("Secondary Entity: " + (action.getSecondaryEntity()==null? "" :
                action.getSecondaryEntity().getName()));
        Label propertyLabel = new Label("Property: " + action.getPropertyName() + " = " + action.getPropertyName() + " + 1");

        VBox detailsBox = new VBox();
        detailsBox.getChildren().addAll(typeLabel, primaryEntityLabel, secondaryEntityLabel, propertyLabel);

        return detailsBox;
    }

    private void accept(ActionDto child) {
        getChildren().add(new ActionItem(child));
    }
}
