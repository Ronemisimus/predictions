package predictions.definition.world.api;

import dto.subdto.show.world.WorldDto;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.environment.api.EnvVariablesManager;
import predictions.rule.api.Rule;
import predictions.termination.api.Termination;

import java.time.Duration;
import java.util.Iterator;
import java.util.Optional;

public interface World {
    EnvVariablesManager getEnvVariablesManager();
    Optional<EntityDefinition> getEntityDefinitionByName(String name);

    Iterator<EntityDefinition> getEntityDefinitions();

    Iterator<Rule> getRules();

    Iterator<Termination> getTerminations();

    void addUserTermination();

    void addTicksTermination(Integer ticks);

    void addTimeTermination(Duration time);

    Integer getSleepTime();

    String getName();

    WorldDto getDto();

    int getGridWidth();

    int getGridHeight();

    void setTerminations(boolean userTermination, Integer ticksLimit, Integer secondsLimit);

    World clone();
}
