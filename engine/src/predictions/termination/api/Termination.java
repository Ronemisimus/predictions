package predictions.termination.api;

import dto.subdto.show.world.TerminationDto;

public interface Termination {
    boolean isTermination(Signal signal);

    TerminationType getTerminationType();

    TerminationDto getDto();
}
