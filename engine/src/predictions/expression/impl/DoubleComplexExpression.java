package predictions.expression.impl;

import predictions.definition.entity.EntityDefinition;
import predictions.execution.context.Context;
import predictions.expression.api.Expression;
import predictions.expression.api.MathOperation;
import predictions.generated.PRDCondition;
import predictions.string.RegexpConstants;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DoubleComplexExpression implements Expression<Double> {

    private Expression<Double> res;
    private Pattern simple_expression_pattern;
    private String expression;

    public DoubleComplexExpression(String expression) {
        this.expression = expression;
        simple_expression_pattern = Pattern.compile(RegexpConstants.SIMPLE_MATH_EXPRESSION_REGEX_STR);
        res = buildExpression(expression);
    }

    private Expression<Double> buildExpression(String expression) {
        Expression<Double> res = null;
        MathOperation op = null;
        Expression<Double> e1;
        Expression<Double> e2;

        String before;
        String match;

        if (isExpressionSimple(expression))
        {
            return buildSimpleExpression(expression);
        }

        int opIndex = getComplexOpIndex(expression);
        if (opIndex!=-1)
        {
            before = expression.substring(0, opIndex);
            match = expression.substring(opIndex,opIndex+1);
            // get string after first match
            expression = expression.substring(opIndex+1);
        }
        else
        {
            before = "";
            match = expression;
            expression = "";
        }
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
        if (res == null) {
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
                }catch (RuntimeException ignored)
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
        boolean matchesPercent = checkPercent(match);
        if (!matchesPercent)
        {
            throw new RuntimeException("Unknown math operation " + match + " in expression" + this.expression);
        }
        MathOperation op = MathOperation.PERCENT;
        match = match.substring(MathOperation.PERCENT.toString().length());
        String exp1 = match.substring(1, match.indexOf(","));
        String exp2 = match.substring(match.indexOf(",")+1, match.length()-1);
        Expression<Double> e1 = buildExpression(exp1);
        Expression<Double> e2 = buildExpression(exp2);
        return new DualMathExpression(op, e1, e2);
    }

    private boolean checkPercent(String match) {
        if(match.startsWith(RegexpConstants.PERCENT_REGEX_STR))
        {
            match = match.substring(RegexpConstants.PERCENT_REGEX_STR.length());
            int level = 0;
            if (match.charAt(0) == '(')
            {
                level = 1;
            }
            for (int i=1; i<match.length(); i++)
            {
                if (match.charAt(i) == '(')
                {
                    level++;
                }
                else if (match.charAt(i) == ')')
                {
                    level--;
                    if (level<0) return false;
                }
                else if (level==0) return false;
            }
            return level == 0;
        }
        return false;
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

        Pattern[] arr = {numberPattern, tickPattern, randomPattern, evaluatePattern, environmentPattern};

        Optional<Matcher> mOpt =  Arrays.stream(arr).map(
                p -> p.matcher(simpleExpression)
        ).filter(Matcher::matches).findFirst();

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
    public Comparable<Double> evaluate(Context context) {
        return res.evaluate(context);
    }
}
