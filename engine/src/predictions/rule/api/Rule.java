package predictions.rule.api;

import dto.subdto.show.world.RuleDto;
import predictions.action.api.Action;

import java.util.List;
import java.util.stream.Collectors;

public interface Rule {
    String getName();
    Activation getActivation();
    List<Action> getActionsToPerform();
    void addAction(Action action);

    default RuleDto getDto() {

        return new RuleDto(getName(),
                getActivation().getCycleSizeInTicks(),
                getActivation().getProbability(),
                getActionsToPerform() == null ? null :
                getActionsToPerform().stream().map(Action::getDto).collect(Collectors.toList()));
    }
}
