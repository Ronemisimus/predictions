package predictions.action.api;

import dto.subdto.show.world.ActionDto;
import predictions.definition.entity.EntityDefinition;
import predictions.execution.context.Context;

public interface Action {
    void invoke(Context context);
    ActionType getActionType();
    EntityDefinition getContextEntity();

    ActionDto getDto();
}
