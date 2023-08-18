package predictions.expression.api;

public enum MathOperation {
    ADD("+") {
        @Override
        public Comparable<Double> evaluate(Comparable<Double> a, Comparable<Double> b) {
            return (Double)a+ (Double)b;
        }
    },
    SUBTRACT("-") {
        @Override
        public Comparable<Double> evaluate(Comparable<Double> a, Comparable<Double> b) {
            return (Double)a- (Double) b;
        }
    },
    MULTIPLY("*") {
        @Override
        public Comparable<Double> evaluate(Comparable<Double> a, Comparable<Double> b) {
            return (Double)a*(Double) b;
        }
    },
    DIVIDE("/") {
        @Override
        public Comparable<Double> evaluate(Comparable<Double> a, Comparable<Double> b) {
            if (b.equals(0.0)) {
                throw new IllegalArgumentException("division by zero");
            }
            return (Double)a/(Double) b;
        }
    },
    PERCENT("percent") {
        @Override
        public Comparable<Double> evaluate(Comparable<Double> a, Comparable<Double> b) {
            if (b.compareTo(0.0)<0 || b.compareTo(100.0)>0) {
                throw new IllegalArgumentException("percent value must be between 0 and 100");
            }
            return (Double)a*(Double) b/100;
        }
    };
    private final String sign;

    MathOperation(String sign) {
        this.sign = sign;
    }

    public abstract Comparable<Double> evaluate(Comparable<Double> a, Comparable<Double> b);

    @Override
    public String toString() {
        return sign;
    }

    public static MathOperation getInstance(String operation) {
        for (MathOperation mathOperation : MathOperation.values()) {
            if (mathOperation.toString().equals(operation)) {
                return mathOperation;
            }
        }
        throw new RuntimeException("Unknown math operation " + operation);
    }
}
