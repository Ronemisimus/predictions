package predictions.definition.value.generator.random.impl.numeric;

public class RandomDoubleGenerator extends AbstractNumericRandomGenerator<Double> {
    protected RandomDoubleGenerator(Double from, Double to) {
        super(from, to);
    }

    @Override
    public Double generateValue() {
        return from + random.nextDouble() * (to - from);
    }
}
