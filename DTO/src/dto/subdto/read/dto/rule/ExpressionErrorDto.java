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
        public Builder() {}

        public Builder withExpression(String expression) {
            this.expression = expression;
            return this;
        }

        public Builder withFunctionName(String functionName) {
            this.functionName = functionName;
            this.functionError = true;
            return this;
        }
        public ExpressionErrorDto build()
        {
            return new ExpressionErrorDto(this);
        }

        public Builder environmentError(String finalExpression1, String type) {
            this.environmentError = true;
            this.notFoundVariable = finalExpression1;
            this.propertyType = type;
            return this;
        }

        public Builder randomTypeError(String type) {
            this.randomTypeError = true;
            this.randomType = type;
            return this;
        }

        public Builder randomParameterError(String expression) {
            this.randomParameterError = true;
            this.randomParameter = expression;
            return this;
        }

        public Builder evaluateError(String expression) {
            this.evaluateError = true;
            this.evaluateExpression = expression;
            return this;
        }

        public Builder missingEntityInContextError(String entity) {
            this.missingEntityInContextError = true;
            this.missingEntityInContext = entity;
            return this;
        }

        public Builder missingPropertyInEntityError(String entity, String prop, String type) {
            this.missingPropertyInEntityError = true;
            this.missingPropertyInEntity = prop;
            this.propertyType = type;
            this.entity = entity;
            return this;
        }

        public Builder percentTypeError(String name) {
            this.percentTypeError = true;
            this.randomType = name;
            return this;
        }

        public Builder ticksTypeError(String name) {
            this.ticksTypeError = true;
            this.randomType = name;
            return this;
        }

        public Builder ticksError(String expression) {
            this.ticksError = true;
            this.evaluateExpression = expression;
            return this;
        }
    }
}
