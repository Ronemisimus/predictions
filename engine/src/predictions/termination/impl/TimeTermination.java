package predictions.termination.impl;

import dto.subdto.show.world.TerminationDto;
import predictions.generated.PRDBySecond;
import predictions.termination.api.Signal;
import predictions.termination.api.Termination;
import predictions.termination.api.TerminationType;

import java.time.Duration;

public class TimeTermination implements Termination {

    private final Duration terminationDuration;

    private static final TerminationType terminationType = TerminationType.TIME;

    public TimeTermination(Duration duration) {
        this.terminationDuration = duration;
    }

    public TimeTermination(PRDBySecond prdTermination) {
        this(Duration.ofSeconds(prdTermination.getCount()));
    }

    @Override
    public boolean isTermination(Signal signal) {
        return Duration.between(signal.getStartTime(), signal.getClock()).compareTo(terminationDuration) >= 0;
    }

    @Override
    public TerminationType getTerminationType() {
        return terminationType;
    }

    @Override
    public TerminationDto getDto() {
        return new TerminationDto(null, Math.toIntExact(this.terminationDuration.getSeconds()), false);
    }

    public Duration getTerminationDuration() {
        return terminationDuration;
    }
}
