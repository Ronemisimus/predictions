package predictions.expression.api;

public enum BooleanOperation {
    AND {
        @Override
        public Boolean evaluate(Boolean a, Boolean b) {
            return a && b;
        }
    },
    OR {
        @Override
        public Boolean evaluate(Boolean a, Boolean b) {
            return a || b;
        }
    };

    public abstract Boolean evaluate(Boolean a, Boolean b);
}
