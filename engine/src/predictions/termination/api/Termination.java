package predictions.termination.api;

public interface Termination {
    boolean isTermination(Signal signal);

    TerminationType getTerminationType();
}
