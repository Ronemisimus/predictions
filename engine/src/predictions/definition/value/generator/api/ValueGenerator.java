package predictions.definition.value.generator.api;

public interface ValueGenerator<T> {
    Comparable<T> generateValue();
}
