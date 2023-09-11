package dto.subdto.read.dto.rule;

public class ActivationErrorDto {
    private final int cycleSizeInTicks;
    private final double probability;
    public ActivationErrorDto(int cycleSizeInTicks, double probability) {
        this.cycleSizeInTicks = cycleSizeInTicks;
        this.probability = probability;
    }

    public int getCycleSizeInTicks() {
        return cycleSizeInTicks;
    }

    public double getProbability() {
        return probability;
    }
}
