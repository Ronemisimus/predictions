package predictions.action.impl;

import dto.subdto.read.dto.rule.ActionErrorDto;
import dto.subdto.show.world.action.ActionDto;
import dto.subdto.show.world.action.KillActionDto;
import predictions.action.api.AbstractAction;
import predictions.action.api.ActionType;
import predictions.action.api.ContextDefinition;
import predictions.execution.context.Context;
import predictions.execution.instance.entity.EntityInstance;

public class KillAction extends AbstractAction {

    private final Boolean killSecondary;
    public KillAction(ContextDefinition contextDefinition,
                      String entityName,
                      ActionErrorDto.Builder builder) {
        super(ActionType.KILL, contextDefinition);
        if (!contextDefinition.getPrimaryEntityDefinition().getName().equals(entityName) &&
                (contextDefinition.getSecondaryEntityDefinition()==null ||
                !contextDefinition.getSecondaryEntityDefinition().getName().equals(entityName)))
        {
            builder.entityNotInContext(entityName);
            throw new RuntimeException("bad entity name");
        }
        killSecondary = contextDefinition.getSecondaryEntityDefinition() !=null &&
                contextDefinition.getSecondaryEntityDefinition().getName().equals(entityName);
    }

    @Override
    public void invoke(Context context) {
        EntityInstance killed = killSecondary?context.getSecondaryEntityInstance():context.getPrimaryEntityInstance();
        if (killed != null)
            context.removeEntity(killed);
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
