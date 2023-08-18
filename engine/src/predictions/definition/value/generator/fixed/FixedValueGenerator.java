package predictions.definition.value.generator.fixed;

import predictions.definition.value.generator.api.ValueGenerator;

public class FixedValueGenerator<T> implements ValueGenerator<T> {

    private final Comparable<T> fixedValue;

    public FixedValueGenerator(Comparable<T> fixedValue) {
        this.fixedValue = fixedValue;
    }

    @Override
    public Comparable<T> generateValue() {
        return fixedValue;
    }
}
