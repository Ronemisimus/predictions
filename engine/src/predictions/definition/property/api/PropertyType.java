package predictions.definition.property.api;

public enum PropertyType {
    DECIMAL {

        @Override
        public Comparable<?> convert(Comparable<?> value) {
            if (!(value instanceof Integer)) {
                throw new IllegalArgumentException("value " + value + " is not of a DECIMAL type (expected Integer class)");
            }
            return value;
        }
    }, BOOLEAN {

        @Override
        public Comparable<?> convert(Comparable<?> value) {
            if (!(value instanceof Boolean)) {
                throw new IllegalArgumentException("value " + value + " is not of a BOOLEAN type (expected Boolean class)");
            }
            return value;
        }
    }, FLOAT {

        @Override
        public Comparable<?> convert(Comparable<?> value) {
            if (!(value instanceof Double)) {
                throw new IllegalArgumentException("value " + value + " is not of a FLOAT type (expected Double class)");
            }
            return value;
        }
    }, STRING {

        @Override
        public Comparable<?> convert(Comparable<?> value) {
            if (!(value instanceof String)) {
                throw new IllegalArgumentException("value " + value + " is not of a STRING type (expected String class)");
            }
            return value;
        }
    };

    public abstract Comparable<?> convert(Comparable<?> value);
}