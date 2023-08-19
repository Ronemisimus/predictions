package dto.subdto.show.world;

import java.util.List;

public class ActionDto {
    private final List<ActionDto> subActions;
    private final String name;

    public ActionDto(List<ActionDto> subActions, String name) {
        this.subActions = subActions;
        this.name = name;
    }

    public List<ActionDto> getSubActions() {
        return subActions;
    }

    public String getName() {
        return name;
    }
}
