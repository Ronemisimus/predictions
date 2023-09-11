package predictions.action.impl;

import dto.subdto.read.dto.rule.ActionErrorDto;
import dto.subdto.show.world.action.ActionDto;
import dto.subdto.show.world.action.KillActionDto;
import predictions.action.api.AbstractAction;
import predictions.action.api.ActionType;
import predictions.action.api.ContextDefinition;
import predictions.definition.entity.EntityDefinition;
import predictions.execution.context.Context;

public class KillAction extends AbstractAction {

    public KillAction(ContextDefinition entityDefinition, ActionErrorDto.Builder builder) {
        super(ActionType.KILL, entityDefinition);
    }

    @Override
    public void invoke(Context context) {
        context.removeEntity(context.getPrimaryEntityInstance());
    }

    @Override
    public ActionDto getDto() {
        return new KillActionDto(
                getContextDefinition().getPrimaryEntityDefinition().getDto(),
                getContextDefinition().getSecondaryEntityDefinition() == null ? null :
                        getContextDefinition().getSecondaryEntityDefinition().getDto()
        );
    }
}
