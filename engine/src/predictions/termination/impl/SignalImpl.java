package predictions.termination.impl;

import predictions.termination.api.Signal;

import java.time.Duration;
import java.time.Instant;

public class SignalImpl implements Signal {

    private final boolean userRequestedTermination;
    private final int world_ticks;
    private final Instant startTime;

    private final Duration duration;

    public SignalImpl(boolean userRequestedTermination, int world_ticks, Instant startTime, Duration duration) {
        this.userRequestedTermination = userRequestedTermination;
        this.world_ticks = world_ticks;
        this.startTime = startTime;
        this.duration = duration;
    }

    @Override
    public Instant getClock() {
        return Instant.now();
    }

    @Override
    public Instant getStartTime() {
        return startTime;
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
