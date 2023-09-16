package predictions.action.impl;

import dto.subdto.read.dto.rule.ActionErrorDto;
import dto.subdto.show.world.action.ActionDto;
import dto.subdto.show.world.action.ReplaceActionDto;
import predictions.action.api.AbstractAction;
import predictions.action.api.ActionType;
import predictions.action.api.ContextDefinition;
import predictions.definition.entity.EntityDefinition;
import predictions.execution.context.Context;
import predictions.execution.instance.entity.EntityInstance;

public class ReplaceAction extends AbstractAction {
    private final EntityDefinition killEntity;
    private final EntityDefinition createEntity;

    private final Boolean secondaryEntity;
    private final String mode;
    public ReplaceAction(ContextDefinition contextDefinition,
                         String kill,
                         String create,
                         String mode,
                         ActionErrorDto.Builder builder) {
        super(ActionType.REPLACE, contextDefinition);
        this.mode = mode;
        if (contextDefinition.getPrimaryEntityDefinition().getName().equals(kill))
        {
            killEntity = contextDefinition.getPrimaryEntityDefinition();
            secondaryEntity = false;
        }
        else if (contextDefinition.getSecondaryEntityDefinition()!=null &&
                contextDefinition.getSecondaryEntityDefinition().getName().equals(kill))
        {
            killEntity = contextDefinition.getSecondaryEntityDefinition();
            secondaryEntity = true;
        }
        else
        {
            secondaryEntity = false;
            killEntity = null;
        }

        createEntity = contextDefinition.getSystemEntityDefinitions().stream()
                .filter(e -> e.getName().equals(create))
                .findFirst().orElse(null);
        if (this.mode==null) {
            throw new RuntimeException("bad mode of replace");
        }
        if (killEntity == null) {
            builder.entityNotInContext(kill);
            throw new RuntimeException("bad entity name");
        }
        if (createEntity == null) {
            builder.entityNotInContext(create);
            throw new RuntimeException("bad entity name");
        }
    }

    @Override
    public void invoke(Context context) {

        // backup killed entity
        EntityInstance source = secondaryEntity? context.getSecondaryEntityInstance():
                context.getPrimaryEntityInstance();

        context.replaceEntity(source.getId(), killEntity, createEntity, mode.equals("derived"));
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
