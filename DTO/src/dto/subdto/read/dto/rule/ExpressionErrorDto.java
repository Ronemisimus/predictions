package dto.subdto.read.dto.rule;

import dto.DTO;

public class ExpressionErrorDto implements DTO {

    private final String expression;

    // error type 1
    private final Boolean functionError;
    private final String functionName;

    // error type 2
    private final Boolean environmentError;
    private final String notFoundVariable;
    private final String propertyType;

    // error type 3
    private final Boolean randomTypeError;
    private final String randomType;

    // error type 4
    private final Boolean randomParameterError;
    private final String randomParameter;

    //error type 5
    private final Boolean evaluateError;
    private final String evaluateExpression;

    //error type 6
    private final Boolean missingEntityInContextError;
    private final String missingEntityInContext;

    //error type 7
    private final Boolean missingPropertyInEntityError;
    private final String missingPropertyInEntity;
    private final String entity;

    //error type 8
    private final Boolean percentTypeError;

    // error type 9
    private final Boolean ticksTypeError;

    // error type 10
    private final Boolean ticksError;

    // error type 11
    private final Boolean badExpressionType;
    private final String expectedType;

    // error type 12
    private final Boolean nullExpressionError;

    private ExpressionErrorDto(Builder builder) {
        this.expression = builder.expression;
        this.functionError = builder.functionError;
        this.functionName = builder.functionName;
        this.environmentError = builder.environmentError;
        this.notFoundVariable = builder.notFoundVariable;
        this.propertyType = builder.propertyType;
        this.randomTypeError = builder.randomTypeError;
        this.randomType = builder.randomType;
        this.randomParameterError = builder.randomParameterError;
        this.randomParameter = builder.randomParameter;
        this.evaluateError = builder.evaluateError;
        this.evaluateExpression = builder.evaluateExpression;
        this.missingEntityInContextError = builder.missingEntityInContextError;
        this.missingEntityInContext = builder.missingEntityInContext;
        this.missingPropertyInEntityError = builder.missingPropertyInEntityError;
        this.missingPropertyInEntity = builder.missingPropertyInEntity;
        this.entity = builder.entity;
        this.percentTypeError = builder.percentTypeError;
        this.ticksTypeError = builder.ticksTypeError;
        this.ticksError = builder.ticksError;
        this.badExpressionType = builder.badExpressionType;
        this.expectedType = builder.expectedType;
        this.nullExpressionError = builder.nullExpressionError;
    }

    public static class Builder {
        private String expression = null;
        private Boolean functionError = false;
        private String functionName = null;
        private Boolean environmentError = false;
        private String notFoundVariable = null;
        private String propertyType = null;
        private Boolean randomTypeError = false;
        private String randomType = null;
        private Boolean randomParameterError = false;
        private String randomParameter = null;
        private Boolean evaluateError = false;
        private String evaluateExpression = null;
        private Boolean missingEntityInContextError = false;
        private String missingEntityInContext = null;
        private Boolean missingPropertyInEntityError = false;
        private String missingPropertyInEntity = null;
        private String entity = null;
        private Boolean percentTypeError = false;
        private Boolean ticksTypeError = false;
        private Boolean ticksError = false;
        private Boolean badExpressionType = false;
        private String expectedType = null;
        private Boolean nullExpressionError = false;
        public Builder() {}

        public Builder withExpression(String expression) {
            this.expression = expression;
            return this;
        }

        public void withFunctionName(String functionName) {
            this.functionName = functionName;
            this.functionError = true;
        }
        public ExpressionErrorDto build()
        {
            return new ExpressionErrorDto(this);
        }

        public void environmentError(String finalExpression1, String type) {
            this.environmentError = true;
            this.notFoundVariable = finalExpression1;
            this.propertyType = type;
        }

        public void randomTypeError(String type) {
            this.randomTypeError = true;
            this.randomType = type;
        }

        public void randomParameterError(String expression) {
            this.randomParameterError = true;
            this.randomParameter = expression;
        }

        public void evaluateError(String expression) {
            this.evaluateError = true;
            this.evaluateExpression = expression;
        }

        public void missingEntityInContextError(String entity) {
            this.missingEntityInContextError = true;
            this.missingEntityInContext = entity;
        }

        public void missingPropertyInEntityError(String entity, String prop, String type) {
            this.missingPropertyInEntityError = true;
            this.missingPropertyInEntity = prop;
            this.propertyType = type;
            this.entity = entity;
        }

        public void percentTypeError(String name) {
            this.percentTypeError = true;
            this.randomType = name;
        }

        public void ticksTypeError(String name) {
            this.ticksTypeError = true;
            this.randomType = name;
        }

        public void ticksError(String expression) {
            this.ticksError = true;
            this.evaluateExpression = expression;
        }

        public void badExpressionType(String type) {
            this.badExpressionType = true;
            this.expectedType = type;
        }

        public void nullExpression() {
            this.nullExpressionError = true;
        }
    }

    public String getExpression() {
        return expression;
    }

    public Boolean getFunctionError() {
        return functionError;
    }

    public String getFunctionName() {
        return functionName;
    }

    public Boolean getEnvironmentError() {
        return environmentError;
    }

    public String getNotFoundVariable() {
        return notFoundVariable;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public Boolean getRandomTypeError() {
        return randomTypeError;
    }

    public String getRandomType() {
        return randomType;
    }

    public Boolean getRandomParameterError() {
        return randomParameterError;
    }

    public String getRandomParameter() {
        return randomParameter;
    }

    public Boolean getEvaluateError() {
        return evaluateError;
    }

    public String getEvaluateExpression() {
        return evaluateExpression;
    }

    public Boolean getMissingEntityInContextError() {
        return missingEntityInContextError;
    }

    public String getMissingEntityInContext() {
        return missingEntityInContext;
    }

    public Boolean getMissingPropertyInEntityError() {
        return missingPropertyInEntityError;
    }

    public String getMissingPropertyInEntity() {
        return missingPropertyInEntity;
    }

    public String getEntity() {
        return entity;
    }

    public Boolean getPercentTypeError() {
        return percentTypeError;
    }

    public Boolean getTicksTypeError() {
        return ticksTypeError;
    }

    public Boolean getTicksError() {
        return ticksError;
    }

    public Boolean getBadExpressionType() {
        return badExpressionType;
    }

    public String getExpectedType() {
        return expectedType;
    }

    public Boolean getNullExpressionError() {
        return nullExpressionError;
    }
}
