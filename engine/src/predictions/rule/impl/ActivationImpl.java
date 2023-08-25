package predictions.rule.impl;

import predictions.generated.PRDActivation;
import predictions.rule.api.Activation;

import java.util.Optional;
import java.util.Random;

public class ActivationImpl implements Activation {

    private final int cycleSizeInTicks;
    private final double probability;

    private final Random rand;

    public ActivationImpl(Integer cycleSizeInTicks, Double probability)
    {
        this.cycleSizeInTicks = cycleSizeInTicks == null? 1 : cycleSizeInTicks;
        this.probability = probability == null? 1 : probability;
        rand = new Random();
    }

    public ActivationImpl(PRDActivation prdActivation) {
        this(
                prdActivation==null? 1 : Optional.ofNullable(prdActivation.getTicks()).orElse(1),
                prdActivation==null? 1. : Optional.ofNullable(prdActivation.getProbability()).orElse(1.));
    }

    @Override
    public boolean isActive(int tickNumber) {
        if (tickNumber%cycleSizeInTicks==0 &&
        tickNumber != 0)
        {
            double coinFlip = rand.nextDouble();
            return coinFlip<probability;
        }
        return false;
    }

    @Override
    public int getCycleSizeInTicks() {
        return this.cycleSizeInTicks;
    }

    @Override
    public double getProbability() {
        return this.probability;
    }
}
