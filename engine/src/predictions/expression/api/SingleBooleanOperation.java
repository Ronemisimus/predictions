package predictions.expression.api;

public enum SingleBooleanOperation {
    EQUAL("=") {
        @Override
        public Boolean evaluate(Comparable<?> a, Comparable<?> b) {
            return a.equals(b);
        }
    },
    NOT_EQUAL("!=") {

        @Override
        public Boolean evaluate(Comparable<?> a, Comparable<?> b) {
            return !a.equals(b);
        }
    },
    BIGGER("bt") {

        @Override
        public Boolean evaluate(Comparable<?> a, Comparable<?> b) {
            //noinspection unchecked
            Comparable<Object> c = (Comparable<Object>) a;
            return c.compareTo(b)> 0;
        }
    },
    SMALLER("lt") {
        @Override
        public Boolean evaluate(Comparable<?> a, Comparable<?> b) {
            //noinspection unchecked
            Comparable<Object> c = (Comparable<Object>) a;
            return c.compareTo(b) < 0;
        }
    };

    public static SingleBooleanOperation getInstance(String name)
    {
        for (SingleBooleanOperation s: SingleBooleanOperation.values())
        {
            if(s.getVal().equals(name))
            {
                return  s;
            }
        }
        return null;
    }

    private final String val;

    SingleBooleanOperation(String val)
    {
        this.val =val;
    }

    public String getVal() {
        return val;
    }

    public abstract Boolean evaluate(Comparable<?> a, Comparable<?> b);
}
