package dto.subdto.show.world;

import java.util.List;

public class RuleDto {
    private final String name;
    private final Integer ticks;
    private final Double probability;
    private final List<ActionDto> actions;

    public RuleDto(String name, Integer ticks, Double probability, List<ActionDto> actions) {
        this.name = name;
        this.ticks = ticks;
        this.probability = probability;
        this.actions = actions;
    }

    public String getName() {
        return name;
    }

    public Integer getTicks() {
        return ticks;
    }

    public Double getProbability() {
        return probability;
    }

    public List<ActionDto> getActions() {
        return actions;
    }
}
