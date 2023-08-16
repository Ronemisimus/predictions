package predictions.termination.api;

import java.time.Instant;

public interface Signal {
    Instant getClock();
    boolean userRequestedTermination();
    int getTicks();
}
