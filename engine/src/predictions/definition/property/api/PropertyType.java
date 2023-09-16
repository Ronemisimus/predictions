package predictions.definition.property.api;

public enum PropertyType {
    DECIMAL(Double.class) {

        @Override
        public void convert(Comparable<?> value) {
            if (!(value instanceof Integer)) {
                throw new IllegalArgumentException("value " + value + " is not of a DECIMAL type (expected Integer class)");
            }
        }
    }, BOOLEAN(Boolean.class) {

        @Override
        public void convert(Comparable<?> value) {
            if (!(value instanceof Boolean)) {
                throw new IllegalArgumentException("value " + value + " is not of a BOOLEAN type (expected Boolean class)");
            }
        }
    }, FLOAT(Double.class) {

        @Override
        public void convert(Comparable<?> value) {
            if (!(value instanceof Double)) {
                throw new IllegalArgumentException("value " + value + " is not of a FLOAT type (expected Double class)");
            }
        }
    }, STRING(String.class) {

        @Override
        public void convert(Comparable<?> value) {
            if (!(value instanceof String)) {
                throw new IllegalArgumentException("value " + value + " is not of a STRING type (expected String class)");
            }
        }
    };

    public final Class<?> type;

    PropertyType(Class<?> type) {
        this.type = type;
    }

    public abstract void convert(Comparable<?> value);
}