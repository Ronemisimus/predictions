package predictions.rule.api;

public interface Activation {
    boolean isActive(int tickNumber);

    int getCycleSizeInTicks();

    double getProbability();
}
