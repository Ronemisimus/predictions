package predictions.termination.impl;

import predictions.termination.api.Signal;
import predictions.termination.api.Termination;
import predictions.termination.api.TerminationType;

import java.time.Duration;
import java.time.Instant;

public class TimeTermination implements Termination {

    private final Instant terminationTime;

    private static final TerminationType terminationType = TerminationType.TIME;

    public TimeTermination(Duration duration) {
        this.terminationTime = Instant.now().plus(duration);
    }

    @Override
    public boolean isTermination(Signal signal) {
        return signal.getClock().compareTo(terminationTime) >= 0;
    }

    @Override
    public TerminationType getTerminationType() {
        return terminationType;
    }
}
