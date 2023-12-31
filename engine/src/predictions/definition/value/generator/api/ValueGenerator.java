package predictions.definition.value.generator.api;

public interface ValueGenerator<T> {
    Comparable<T> generateValue();

    boolean isRandomInit();

    Comparable<T> getInitValue();
}
