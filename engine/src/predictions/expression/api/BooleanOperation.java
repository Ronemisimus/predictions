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

    public abstract Boolean evaluate(Comparable<Boolean> a, Comparable<Boolean> b);
}
