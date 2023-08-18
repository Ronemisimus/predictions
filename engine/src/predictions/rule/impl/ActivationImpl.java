package predictions.rule.impl;

import predictions.rule.api.Activation;

import java.util.Random;

public class ActivationImpl implements Activation {

    private final int cycleSizeInTicks;
    private final double probability;

    private Random rand;

    public ActivationImpl(int cycleSizeInTicks, double probability)
    {
        this.cycleSizeInTicks = cycleSizeInTicks;
        this.probability = probability;
        rand = new Random();
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
}
