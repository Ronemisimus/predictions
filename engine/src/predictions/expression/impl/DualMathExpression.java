package predictions.expression.impl;

import predictions.execution.context.Context;
import predictions.expression.api.DualExpression;
import predictions.expression.api.Expression;
import predictions.expression.api.MathOperation;

public class DualMathExpression extends DualExpression<Double> {

    private final MathOperation mathOperation;

    public DualMathExpression(MathOperation mathOperation, Expression<Double> a, Expression<Double> b) {
        super(a, b);
        this.mathOperation = mathOperation;
    }

    @Override
    public Comparable<Double> evaluate(Context context) {
        Comparable<?> exp1_res = getExpression1().evaluate(context);
        Comparable<?> exp2_res = getExpression2().evaluate(context);
        Comparable<Double> exp1_double;
        Comparable<Double> exp2_double;
        if(exp1_res instanceof Integer)
        {
            exp1_double = Double.valueOf((Integer) exp1_res);
        }
        else
        {
            exp1_double = (Double) exp1_res;
        }

        if (exp2_res instanceof Integer)
        {
            exp2_double = Double.valueOf((Integer) exp2_res);
        }
        else
            exp2_double = (Double) exp2_res;
        return mathOperation.evaluate(exp1_double,exp2_double);
    }

    @Override
    public String toString() {
        return "( " + getExpression1().toString() + " ) " + mathOperation.toString() + " ( " + getExpression2().toString() + " )";
    }
}
