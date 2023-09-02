package dto.subdto.show.world;

import java.util.List;

public class WorldDto {
    private final List<PropertyDto> environment;
    private final List<EntityDto> entities;

    private final List<RuleDto> rules;

    private final Integer ticksTermination;
    private final Integer timeTermination;

    private final boolean userTermination;

    private final Integer gridWidth;

    private final Integer gridHeight;

    private final Integer threadCount;

    public WorldDto(List<PropertyDto> environment,
                    List<EntityDto> entities,
                    List<RuleDto> rules,
                    Integer tickTermination,
                    Integer timeTermination,
                    Boolean userTermination,
                    Integer gridWidth,
                    Integer gridHeight,
                    Integer threadCount) {
        this.environment = environment;
        this.entities = entities;
        this.rules = rules;
        this.userTermination = userTermination!=null && userTermination;
        this.timeTermination = timeTermination;
        this.ticksTermination = tickTermination;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.threadCount = threadCount;
    }

    public List<PropertyDto> getEnvironment() {
        return environment;
    }

    public List<EntityDto> getEntities() {
        return entities;
    }

    public List<RuleDto> getRules() {
        return rules;
    }

    public Integer getTicksTermination() {
        return ticksTermination;
    }

    public Integer getTimeTermination() {
        return timeTermination;
    }

    public boolean isUserTermination() {
        return userTermination;
    }

    public Integer getGridWidth() {
        return gridWidth;
    }

    public Integer getGridHeight() {
        return gridHeight;
    }

    public Integer getThreadCount() {
        return threadCount;
    }
}
