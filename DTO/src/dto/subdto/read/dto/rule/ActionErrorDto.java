package dto.subdto.read.dto.rule;

import dto.DTO;

public class ActionErrorDto implements DTO {

    private final ExpressionErrorDto expressionErrorDto;

    private final Boolean missingPropertyInEntityError;
    private final String property;

    private final Boolean entityNotInContext;
    private final String entityName;

    private final Boolean propertyTypeMismatch;
    private final String actionType;
    private ActionErrorDto(Builder builder)
    {
        this.expressionErrorDto = builder.expressionErrorDto;
        this.missingPropertyInEntityError = builder.missingPropertyInEntityError;
        this.property = builder.property;
        this.entityNotInContext = builder.entityNotInContext;
        this.entityName = builder.entityName;
        this.propertyTypeMismatch = builder.propertyTypeMismatch;
        this.actionType = builder.actionType;
    }

    public static class Builder {
        private ExpressionErrorDto expressionErrorDto = null;
        private Boolean missingPropertyInEntityError = null;
        private String property = null;
        private Boolean entityNotInContext = null;
        private String entityName = null;
        private Boolean propertyTypeMismatch = null;
        private String actionType = null;

        public Builder() {

        }
        public ActionErrorDto build() {
            return new ActionErrorDto(this);
        }

        public Builder expressionError(ExpressionErrorDto build) {
            this.expressionErrorDto = build;
            return this;
        }

        public Builder propertyNotInContext(String property) {
            this.property = property;
            this.missingPropertyInEntityError = true;
            return this;
        }

        public Builder entityNotInContext(String entityName) {
            this.entityNotInContext = true;
            this.entityName = entityName;
            return this;
        }

        public Builder propertyTypeMismatch(String property, String name) {
            this.propertyTypeMismatch = true;
            this.property = property;
            this.entityName = name;
            return this;
        }
    }
}
