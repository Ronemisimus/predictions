package predictions.termination.impl;

import dto.subdto.show.world.TerminationDto;
import predictions.termination.api.Signal;
import predictions.termination.api.Termination;
import predictions.termination.api.TerminationType;

public class UserTermination implements Termination {
    @Override
    public boolean isTermination(Signal signal) {
        return false;
    }

    @Override
    public TerminationType getTerminationType() {
        return null;
    }

    @Override
    public TerminationDto getDto() {
        return new TerminationDto(null, null, true);
    }
}
