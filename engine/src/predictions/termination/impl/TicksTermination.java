package predictions.termination.impl;

import predictions.termination.api.Signal;
import predictions.termination.api.Termination;

public class TicksTermination implements Termination {

    private int ticks;

    public TicksTermination(int ticks) {
        this.ticks = ticks;
    }

    @Override
    public boolean isTermination(Signal signal) {
        return signal.getTicks() >= ticks;
    }
}
