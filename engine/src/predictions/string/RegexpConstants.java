package predictions.string;

public class RegexpConstants {

    public static final String simpleStrExpectingClose = "[^\\)]+?";
    public static final String simpleStrExpectingDot = "[^\\.]+?";
    public static final String simpleStrExpectingComma = "[^,]+?";
    public static final String TICK_REGEX_STR = "ticks\\("+ simpleStrExpectingDot +"\\."+ simpleStrExpectingClose +"\\)";
    public static final String RANDOM_REGEX_STR = "random\\(\\d*\\)";
    public static final String EVALUATE_REGEX_STR = "evaluate\\("+ simpleStrExpectingDot +"\\."+ simpleStrExpectingClose +"\\)";
    public static final String ENVIRONMENT_REGEX_STR = "environment\\("+ simpleStrExpectingClose +"\\)";
    public static final String NUMBER_REGEX_STR = "\\d+\\.?\\d*";
    public static final String SIMPLE_MATH_EXPRESSION_REGEX_STR = "^("+TICK_REGEX_STR+
             "|"+RANDOM_REGEX_STR+"|"+EVALUATE_REGEX_STR+"|"+ENVIRONMENT_REGEX_STR+
             "|"+NUMBER_REGEX_STR+")$";
    public static final String PERCENT_REGEX_STR = "percent";


}
