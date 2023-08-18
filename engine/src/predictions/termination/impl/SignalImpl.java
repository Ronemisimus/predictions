package predictions.termination.impl;

import predictions.termination.api.Signal;

import java.time.Instant;

public class SignalImpl implements Signal {

    private final boolean userRequestedTermination;
    private final int world_ticks;

    public SignalImpl(boolean userRequestedTermination, int world_ticks) {
        this.userRequestedTermination = userRequestedTermination;
        this.world_ticks = world_ticks;
    }

    @Override
    public Instant getClock() {
        return Instant.now();
    }

    @Override
    public boolean userRequestedTermination() {
        return userRequestedTermination;
    }

    @Override
    public int getTicks() {
        return world_ticks;
    }
}
