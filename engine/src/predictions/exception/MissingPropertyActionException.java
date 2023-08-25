package predictions.exception;

import predictions.action.api.ActionType;

public class MissingPropertyActionException extends Throwable {

    private final String property;
    private final ActionType actionType;
    public MissingPropertyActionException(String property, ActionType actionType) {
        this.property = property;
        this.actionType = actionType;
    }

    public String getProperty() {
        return property;
    }

    public ActionType getActionType() {
        return actionType;
    }
}
