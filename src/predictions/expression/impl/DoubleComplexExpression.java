package predictions.expression.impl;

import predictions.execution.context.Context;
import predictions.expression.api.Expression;
import predictions.expression.api.MathOperation;
import predictions.string.RegexpConstants;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DoubleComplexExpression implements Expression<Double> {

    private Expression<Double> res;
    private Pattern simple_expression_pattern;
    private Pattern complex_expression_pattern;
    private String expression;

    public DoubleComplexExpression(String expression) {
        this.expression = expression;
        simple_expression_pattern = Pattern.compile(RegexpConstants.SIMPLE_MATH_EXPRESSION_REGEX_STR);
        complex_expression_pattern = Pattern.compile(RegexpConstants.COMPLEX_MATH_REGEX_STR);
        res = buildExpression(expression);
    }

    private Expression<Double> buildExpression(String expression) {
        Expression<Double> res = null;
        MathOperation op = null;
        Expression<Double> e1;
        Expression<Double> e2;

        if (isExpressionSimple(expression))
        {
            return buildSimpleExpression(expression);
        }

        int opIndex = getComplexOpIndex(expression);
        String before = expression.substring(0, opIndex);
        String match = expression.substring(opIndex,opIndex+1);
        // get string after first match
        expression = expression.substring(opIndex+1);

        // analyze operation
        try {
            op = MathOperation.getInstance(match);
        }catch (RuntimeException e)
        {
            // match can be of shape percent(e1,e2)
            try {
                res = catchPercent(match);
            }catch (RuntimeException ex)
            {
                throw new RuntimeException("Unknown math operation " + match + " in expression" + this.expression);
            }
        }

        // build sub expressions
        if (res == null && op != null) {
            if (isExpressionSimple(before)) e1 = buildSimpleExpression(before);
            else e1 = buildExpression(before);
            if (isExpressionSimple(expression)) e2 = buildSimpleExpression(expression);
            else e2 = buildExpression(expression);
            res = new DualMathExpression(op, e1, e2);
        }
        return res;
    }

    private int getComplexOpIndex(String expression) {
        int level = 0;
        for (int pos =0; pos<expression.length(); pos++)
        {
            if (expression.charAt(pos) == '(')
            {
                level++;
            }
            else if (expression.charAt(pos) == ')')
            {
                level--;
                if (level<0) throw new RuntimeException("bad parenthesis in expression " + expression);
            }
            else if (level ==0)
            {
                String sign = expression.substring(pos,pos+1);
                MathOperation op = null;
                try {
                    op = MathOperation.getInstance(sign);
                }catch (RuntimeException e)
                {

                }
                if (op!=null)
                {
                    return pos;
                }
            }
        }
        return -1;
    }

    private Expression<Double> catchPercent(String match) {
        Matcher percentMatcher = Pattern.compile(RegexpConstants.PERCENT_REGEX_STR).matcher(match);
        if (!percentMatcher.matches())
        {
            throw new RuntimeException("Unknown math operation " + match + " in expression" + this.expression);
        }
        MathOperation op = MathOperation.PERCENT;
        match = match.substring(MathOperation.PERCENT.toString().length());
        String exp1 = match.substring(1, match.indexOf(","));
        String exp2 = match.substring(match.indexOf(",")+1);
        Expression<Double> e1 = buildExpression(exp1);
        Expression<Double> e2 = buildExpression(exp2);
        return new DualMathExpression(op, e1, e2);
    }

    private boolean isExpressionSimple(String expression){
        return simple_expression_pattern.matcher(expression).matches();
    }

    private Expression<Double> buildSimpleExpression(String simpleExpression) {
        Pattern numberPattern = Pattern.compile(RegexpConstants.NUMBER_REGEX_STR);
        Pattern tickPattern = Pattern.compile(RegexpConstants.TICK_REGEX_STR);
        Pattern randomPattern = Pattern.compile(RegexpConstants.RANDOM_REGEX_STR);
        Pattern evaluatePattern = Pattern.compile(RegexpConstants.EVALUATE_REGEX_STR);
        Pattern environmentPattern = Pattern.compile(RegexpConstants.ENVIRONMENT_REGEX_STR);

        Pattern arr[] = {numberPattern, tickPattern, randomPattern, evaluatePattern, environmentPattern};

        Optional<Matcher> mOpt =  Arrays.stream(arr).map(
                p -> p.matcher(simpleExpression)
        ).filter(m -> m.matches()).findFirst();

        if (!mOpt.isPresent()) throw new RuntimeException("Unknown basic expression " + simpleExpression + " in expression" + this.expression);

        Pattern pattern = mOpt.get().pattern();
        if (pattern.equals(numberPattern)) {
            return new NumberExpression(Double.parseDouble(mOpt.get().group(0)));
        } else if (pattern.equals(tickPattern)) {
            return TicksExpression.BuildInstance(simpleExpression);
        } else if (pattern.equals(randomPattern)) {
            return RandomExpression.BuildInstance(simpleExpression);
        } else if (pattern.equals(evaluatePattern)) {
            return PropertyExpression.BuildDoubleInstance(simpleExpression);
        } else if (pattern.equals(environmentPattern)) {
            return NumericalEnviromentExpression.BuildDoubleInstance(simpleExpression);
        }
        throw new RuntimeException("Unknown basic expression " + simpleExpression + " in expression" + this.expression);

    }


    @Override
    public Double evaluate(Context context) {
        return res.evaluate(context);
    }
}
