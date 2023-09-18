package predictions.expression.api;

public enum BooleanOperation {
    AND {
        @Override
        public Boolean evaluate(Comparable<Boolean> a, Comparable<Boolean> b) {
            return (Boolean) a && (Boolean) b;
        }
    },
    OR {
        @Override
        public Boolean evaluate(Comparable<Boolean> a, Comparable<Boolean> b) {
            return (Boolean) a || (Boolean) b;
        }
    };

    public static BooleanOperation getInstance(String upperCase) {
        if (AND.toString().equalsIgnoreCase(upperCase)) {
            return AND;
        } else if (OR.toString().equalsIgnoreCase(upperCase)) {
            return OR;
        }
        return null;
    }

    public abstract Boolean evaluate(Comparable<Boolean> a, Comparable<Boolean> b);
}
