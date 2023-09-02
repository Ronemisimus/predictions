package predictions.definition.property.api;

public enum PropertyType {
    DECIMAL(Double.class) {

        @Override
        public Comparable<?> convert(Comparable<?> value) {
            if (!(value instanceof Integer)) {
                throw new IllegalArgumentException("value " + value + " is not of a DECIMAL type (expected Integer class)");
            }
            return value;
        }
    }, BOOLEAN(Boolean.class) {

        @Override
        public Comparable<?> convert(Comparable<?> value) {
            if (!(value instanceof Boolean)) {
                throw new IllegalArgumentException("value " + value + " is not of a BOOLEAN type (expected Boolean class)");
            }
            return value;
        }
    }, FLOAT(Double.class) {

        @Override
        public Comparable<?> convert(Comparable<?> value) {
            if (!(value instanceof Double)) {
                throw new IllegalArgumentException("value " + value + " is not of a FLOAT type (expected Double class)");
            }
            return value;
        }
    }, STRING(String.class) {

        @Override
        public Comparable<?> convert(Comparable<?> value) {
            if (!(value instanceof String)) {
                throw new IllegalArgumentException("value " + value + " is not of a STRING type (expected String class)");
            }
            return value;
        }
    };

    public final Class type;

    PropertyType(Class type) {
        this.type = type;
    }

    public abstract Comparable<?> convert(Comparable<?> value);
}