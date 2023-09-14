package predictions.action.api;

import dto.subdto.show.world.action.ActionDto;
import predictions.execution.context.Context;

public interface Action {
    void invoke(Context context);
    ActionType getActionType();
    ContextDefinition getContextDefinition();
    ActionDto getDto();
}
