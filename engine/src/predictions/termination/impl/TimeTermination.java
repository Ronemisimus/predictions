package predictions.termination.impl;

import predictions.termination.api.Signal;
import predictions.termination.api.Termination;

import java.time.Duration;
import java.time.Instant;

public class TimeTermination implements Termination {

    private Instant terminationTime;

    public TimeTermination(Duration duration) {
        this.terminationTime = Instant.now().plus(duration);
    }

    @Override
    public boolean isTermination(Signal signal) {
        return signal.getClock().compareTo(terminationTime) >= 0;
    }
}
