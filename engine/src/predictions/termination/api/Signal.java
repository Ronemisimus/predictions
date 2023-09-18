package predictions.termination.api;

import java.time.Duration;

public interface Signal {
    Duration getDuration();
    boolean userRequestedTermination();
    int getTicks();
}
