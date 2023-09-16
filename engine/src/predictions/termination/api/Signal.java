package predictions.termination.api;

import java.time.Duration;
import java.time.Instant;

public interface Signal {
    Instant getClock();
    public Duration getDuration();
    Instant getStartTime();
    boolean userRequestedTermination();
    int getTicks();
}
