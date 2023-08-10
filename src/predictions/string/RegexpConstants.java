package predictions.string;

public class RegexpConstants {

    public static final String nonComplexString = "[^\\)]+?";
    public static final String TICK_REGEX_STR = "ticks\\("+nonComplexString+"\\."+nonComplexString+"\\)";
    public static final String RANDOM_REGEX_STR = "random\\(\\d*\\)";
    public static final String EVALUATE_REGEX_STR = "evaluate\\("+nonComplexString+"\\."+nonComplexString+"\\)";
    public static final String ENVIRONMENT_REGEX_STR = "environment\\("+nonComplexString+"\\)";
    public static final String NUMBER_REGEX_STR = "\\d+\\.?\\d*";
    public static final String SIMPLE_MATH_EXPRESSION_REGEX_STR = "^("+TICK_REGEX_STR+
             "|"+RANDOM_REGEX_STR+"|"+EVALUATE_REGEX_STR+"|"+ENVIRONMENT_REGEX_STR+
             "|"+NUMBER_REGEX_STR+")$";
    public static final String PLUS_REGEX_STR = "\\+";
    public static final String MINUS_REGEX_STR = "-";
    public static final String MULTIPLY_REGEX_STR = "\\*";
    public static final String DIVIDE_REGEX_STR = "/";

    public static final String PERCENT_OPS_REGEX_STR = "\\(.*?,.*?\\)";
    public static final String PERCENT_REGEX_STR = "^percent"+PERCENT_OPS_REGEX_STR+"$";
    public static final String COMPLEX_MATH_REGEX_STR = "(" + PLUS_REGEX_STR + "|" +
             MINUS_REGEX_STR + "|" + MULTIPLY_REGEX_STR +
             "|" + DIVIDE_REGEX_STR + "|" + PERCENT_REGEX_STR +
             "|" + SIMPLE_MATH_EXPRESSION_REGEX_STR + ")";


}
