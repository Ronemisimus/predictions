package predictions.termination.impl;

import dto.subdto.show.world.TerminationDto;
import predictions.generated.PRDByTicks;
import predictions.termination.api.Signal;
import predictions.termination.api.Termination;
import predictions.termination.api.TerminationType;

public class TicksTermination implements Termination {

    private final int ticks;

    private static final TerminationType terminationType = TerminationType.TICKS;

    public TicksTermination(int ticks) {
        this.ticks = ticks;
    }

    public TicksTermination(PRDByTicks prdTermination) {
        this(prdTermination.getCount());
    }

    @Override
    public boolean isTermination(Signal signal) {
        return signal.getTicks() >= ticks;
    }

    @Override
    public TerminationType getTerminationType() {
        return terminationType;
    }

    @Override
    public TerminationDto getDto() {
        return new TerminationDto(this.ticks, null, false);
    }

    public int getTicks() {
        return ticks;
    }
}
