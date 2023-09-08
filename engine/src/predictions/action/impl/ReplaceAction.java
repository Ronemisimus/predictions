package predictions.action.impl;

import dto.subdto.show.world.action.ActionDto;
import dto.subdto.show.world.action.ReplaceActionDto;
import predictions.action.api.AbstractAction;
import predictions.action.api.Action;
import predictions.action.api.ActionType;
import predictions.action.api.ContextDefinition;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.property.api.PropertyDefinition;
import predictions.execution.context.Context;
import predictions.execution.grid.Coordinate;
import predictions.execution.instance.entity.EntityInstance;

public class ReplaceAction extends AbstractAction {

    private final EntityDefinition killEntity;
    private final EntityDefinition createEntity;
    private final String mode;
    public ReplaceAction(ContextDefinition contextDefinition, String kill, String create, String mode) {
        super(ActionType.REPLACE, contextDefinition);
        EntityDefinition createEntity1;
        killEntity = contextDefinition.getPrimaryEntityDefinition().getName().equals(kill)?
                contextDefinition.getPrimaryEntityDefinition() :
                contextDefinition.getSecondaryEntityDefinition();
        if (contextDefinition.getSecondaryEntityDefinition()!=null
        && contextDefinition.getSecondaryEntityDefinition().getName().equals(create))
        {
            createEntity1 = contextDefinition.getSecondaryEntityDefinition();
        }
        else if(contextDefinition.getPrimaryEntityDefinition().getName().equals(create)){
            createEntity1 = contextDefinition.getPrimaryEntityDefinition();
        }
        else{
            createEntity1 = contextDefinition.getSystemEntityDefinitions().stream()
                    .filter(e -> e.getName().equals(create))
                    .findFirst().orElse(null);
        }
        createEntity = createEntity1;
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

        // backup killed entity
        EntityInstance source = context.getPrimaryEntityInstance();
        Coordinate location = source.getLocation();

        // kill entity
        context.removeEntity(source);

        // create new entity
        EntityInstance target = context.createEntity(createEntity);

        if (!mode.equals("scratch")) {
            target.setLocation(source.getLocation());

            createEntity.getProps().stream()
                    .map(PropertyDefinition::getName)
                    .filter(name -> killEntity.getProps().stream()
                            .anyMatch(p1 -> p1.getName().equals(name)))
                    .forEach(name -> target.getPropertyByName(name)
                            .updateValue(
                                    source.getPropertyByName(name).getValue(),
                                    context.getTick()
                            )
                    );
        }
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
