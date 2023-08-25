package predictions.definition.value.generator.random.impl.numeric;

public class RandomDoubleGenerator extends AbstractNumericRandomGenerator<Double> {
    public RandomDoubleGenerator(Double from, Double to) {
        super(from, to);
    }

    @Override
    public Comparable<Double> generateValue() {
        return from + random.nextDouble() * (to - from);
    }
}
