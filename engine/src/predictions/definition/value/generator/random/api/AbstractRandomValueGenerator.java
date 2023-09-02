package predictions.definition.value.generator.random.api;

import predictions.definition.value.generator.api.ValueGenerator;

import java.util.Random;

public abstract class AbstractRandomValueGenerator<T> implements ValueGenerator<T> {
    protected final Random random;

    protected AbstractRandomValueGenerator() {
        random = new Random();
    }

    @Override
    public boolean isRandomInit() {
        return true;
    }

    @Override
    public Comparable<T> getInitValue() {
        return null;
    }
}
