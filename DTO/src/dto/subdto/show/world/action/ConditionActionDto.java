package dto.subdto.show.world.action;

import dto.subdto.show.world.EntityDto;

import java.util.List;

public class ConditionActionDto extends ActionDto{
    private final String conditionExpression;
    private final List<ActionDto> thenActions;
    private final List<ActionDto> elseActions;

    public ConditionActionDto(EntityDto primaryEntity,
                              EntityDto secondaryEntity,
                              String conditionExpression,
                              List<ActionDto> thenActions,
                              List<ActionDto> elseActions) {
        super("Condition", primaryEntity, secondaryEntity);
        this.conditionExpression = conditionExpression;
        this.thenActions = thenActions;
        this.elseActions = elseActions;
    }

    public String getConditionExpression() {
        return conditionExpression;
    }

    public List<ActionDto> getThenActions() {
        return thenActions;
    }

    public List<ActionDto> getElseActions() {
        return elseActions;
    }
}
