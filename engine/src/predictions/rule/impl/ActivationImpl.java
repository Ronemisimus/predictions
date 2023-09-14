package predictions.rule.impl;

import dto.subdto.read.dto.rule.ActivationErrorDto;
import dto.subdto.read.dto.rule.RuleErrorDto;
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

    public ActivationImpl(PRDActivation prdActivation, RuleErrorDto.Builder builder) {
        this(prdActivation==null? 1 : Optional.ofNullable(prdActivation.getTicks()).orElse(1),
                prdActivation==null? 1. : Optional.ofNullable(prdActivation.getProbability()).orElse(1.));
        if (cycleSizeInTicks<=0 || probability<0 || probability>1)
        {
            builder.message("Invalid cycle size or probability")
                    .activationError(
                new ActivationErrorDto(cycleSizeInTicks,probability)
            );
            throw new RuntimeException("Invalid cycle size or probability");
        }
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
