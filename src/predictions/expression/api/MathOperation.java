package predictions.expression.api;

public enum MathOperation {
    ADD("+") {
        @Override
        public Double evaluate(Double a, Double b) {
            return a+b;
        }
    },
    SUBTRACT("-") {
        @Override
        public Double evaluate(Double a, Double b) {
            return a-b;
        }
    },
    MULTIPLY("*") {
        @Override
        public Double evaluate(Double a, Double b) {
            return a*b;
        }
    },
    DIVIDE("/") {
        @Override
        public Double evaluate(Double a, Double b) {
            if (b == 0) {
                throw new IllegalArgumentException("division by zero");
            }
            return a/b;
        }
    },
    PERCENT("percent") {
        @Override
        public Double evaluate(Double a, Double b) {
            if (b<0 || b>100) {
                throw new IllegalArgumentException("percent value must be between 0 and 100");
            }
            return a*b/100;
        }
    };
    private String sign;

    private MathOperation(String sign) {
        this.sign = sign;
    }

    public abstract Double evaluate(Double a, Double b);

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
