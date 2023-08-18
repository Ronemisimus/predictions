package predictions.termination.impl;

import predictions.termination.api.Signal;
import predictions.termination.api.Termination;
import predictions.termination.api.TerminationType;

public class TicksTermination implements Termination {

    private final int ticks;

    private static final TerminationType terminationType = TerminationType.TICKS;

    public TicksTermination(int ticks) {
        this.ticks = ticks;
    }

    @Override
    public boolean isTermination(Signal signal) {
        return signal.getTicks() >= ticks;
    }

    @Override
    public TerminationType getTerminationType() {
        return terminationType;
    }
}
