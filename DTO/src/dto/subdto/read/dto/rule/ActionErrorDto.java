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

    private final Boolean noPrimaryEntity;

    private final Boolean noEntityNamed;
    private ActionErrorDto(Builder builder)
    {
        this.expressionErrorDto = builder.expressionErrorDto;
        this.missingPropertyInEntityError = builder.missingPropertyInEntityError;
        this.property = builder.property;
        this.entityNotInContext = builder.entityNotInContext;
        this.entityName = builder.entityName;
        this.propertyTypeMismatch = builder.propertyTypeMismatch;
        this.actionType = builder.actionType;
        this.noPrimaryEntity = builder.noPrimaryEntity;
        this.noEntityNamed = builder.noEntityNamed;
    }

    public static class Builder {
        private ExpressionErrorDto expressionErrorDto = null;
        private Boolean missingPropertyInEntityError = false;
        private String property = null;
        private Boolean entityNotInContext = false;
        private String entityName = null;
        private Boolean propertyTypeMismatch = false;
        private String actionType = null;
        private Boolean noPrimaryEntity = false;
        private Boolean noEntityNamed = false;

        public Builder() {
        }

        public Builder actionType(String actionType) {
            this.actionType = actionType;
            return this;
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

        public void noPrimaryEntity() {
            this.noPrimaryEntity = true;
        }

        public void noEntityNamed(String primaryEntityName) {
            this.noEntityNamed = true;
            this.entityName = primaryEntityName;
        }
    }

    public ExpressionErrorDto getExpressionErrorDto() {
        return expressionErrorDto;
    }

    public Boolean getMissingPropertyInEntityError() {
        return missingPropertyInEntityError;
    }

    public String getProperty() {
        return property;
    }

    public Boolean getEntityNotInContext() {
        return entityNotInContext;
    }

    public String getEntityName() {
        return entityName;
    }

    public Boolean getPropertyTypeMismatch() {
        return propertyTypeMismatch;
    }

    public String getActionType() {
        return actionType;
    }

    public Boolean getNoPrimaryEntity() {
        return noPrimaryEntity;
    }

    public Boolean getNoEntityNamed() {
        return noEntityNamed;
    }
}
