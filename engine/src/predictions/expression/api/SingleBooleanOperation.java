package predictions.expression.api;

public enum SingleBooleanOperation {
    EQUAL {
        @Override
        public Boolean evaluate(Comparable a, Comparable b) {
            return a.equals(b);
        }
    },
    NOT_EQUAL {
        @Override
        public Boolean evaluate(Comparable a, Comparable b) {
            return !a.equals(b);
        }
    },
    BIGGER {
        @Override
        public Boolean evaluate(Comparable a, Comparable b) {
            return a.compareTo(b) > 0;
        }
    },
    SMALLER {
        @Override
        public Boolean evaluate(Comparable a, Comparable b) {
            return a.compareTo(b) < 0;
        }
    };
    public abstract Boolean evaluate(Comparable a, Comparable b);
}
