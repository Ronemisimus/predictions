package predictions.termination.api;

import java.time.Instant;

public interface Signal {
    Instant getClock();

    Instant getStartTime();
    boolean userRequestedTermination();
    int getTicks();
}
