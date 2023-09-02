package predictions.action.impl;

import dto.subdto.show.world.action.ActionDto;
import dto.subdto.show.world.action.ReplaceActionDto;
import predictions.action.api.AbstractAction;
import predictions.action.api.Action;
import predictions.action.api.ActionType;
import predictions.action.api.ContextDefinition;
import predictions.definition.entity.EntityDefinition;
import predictions.execution.context.Context;

public class ReplaceAction extends AbstractAction {

    private final EntityDefinition killEntity;
    private final EntityDefinition createEntity;
    private final String mode;
    public ReplaceAction(ContextDefinition contextDefinition, String kill, String create, String mode) {
        super(ActionType.REPLACE, contextDefinition);
        killEntity = contextDefinition.getPrimaryEntityDefinition().getName().equals(kill)?
                contextDefinition.getPrimaryEntityDefinition() :
                contextDefinition.getSecondaryEntityDefinition();
        createEntity = contextDefinition.getPrimaryEntityDefinition().getName().equals(kill)?
                contextDefinition.getSecondaryEntityDefinition():
                contextDefinition.getPrimaryEntityDefinition();
        this.mode = mode.equalsIgnoreCase("scratch")?
        mode: mode.equalsIgnoreCase("derived")? mode:
        null;
        if (this.mode==null)
            throw new RuntimeException("bad mode of replace");
        if (killEntity == null || createEntity == null)
            throw new RuntimeException("one of these entities missing from context:" + kill +", " +create);
    }

    @Override
    public void invoke(Context context) {

    }

    public EntityDefinition getKillEntity() {
        return killEntity;
    }

    public EntityDefinition getCreateEntity() {
        return createEntity;
    }

    public String getMode() {
        return mode;
    }

    @Override
    public ActionDto getDto() {
        return new ReplaceActionDto(
                killEntity.getDto(),
                createEntity.getDto(),
                mode
        );
    }
}
