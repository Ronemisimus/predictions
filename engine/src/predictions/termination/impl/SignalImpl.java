package predictions.termination.impl;

import predictions.termination.api.Signal;

import java.time.Duration;

public class SignalImpl implements Signal {

    private final boolean userRequestedTermination;
    private final int world_ticks;

    private final Duration duration;

    public SignalImpl(boolean userRequestedTermination, int world_ticks, Duration duration) {
        this.userRequestedTermination = userRequestedTermination;
        this.world_ticks = world_ticks;
        this.duration = duration;
    }

    @Override
    public boolean userRequestedTermination() {
        return userRequestedTermination;
    }

    @Override
    public int getTicks() {
        return world_ticks;
    }

    public Duration getDuration() {
        return duration;
    }
}
