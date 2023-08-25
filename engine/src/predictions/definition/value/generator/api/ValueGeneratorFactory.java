package predictions.definition.value.generator.api;

import predictions.definition.value.generator.fixed.FixedValueGenerator;
import predictions.definition.value.generator.random.impl.bool.RandomBooleanValueGenerator;
import predictions.definition.value.generator.random.impl.numeric.RandomDoubleGenerator;
import predictions.definition.value.generator.random.impl.numeric.RandomIntegerGenerator;

public interface ValueGeneratorFactory {

    static <T> ValueGenerator<T> createFixed(Comparable<T> value) {
        return new FixedValueGenerator<>(value);
    }

    static ValueGenerator<Boolean> createRandomBoolean() {
        return new RandomBooleanValueGenerator();
    }

    static ValueGenerator<Integer> createRandomInteger(Integer from, Integer to) {
        return new RandomIntegerGenerator(from, to);
    }

    static ValueGenerator<Double> createRandomDouble(Double from, Double to) {
        return new RandomDoubleGenerator(from,to);
    }
}
