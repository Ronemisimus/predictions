package dto.subdto.read.dto.rule;

import dto.DTO;

public class RuleErrorDto implements DTO {

    private final ActivationErrorDto activationErrorDto;
    private final ExpressionErrorDto expressionErrorDto;
    private final ActionErrorDto actionErrorDto;

    private RuleErrorDto(Builder builder)
    {
        this.activationErrorDto = builder.activationErrorDto;
        this.expressionErrorDto = builder.expressionErrorDto;
        this.actionErrorDto = builder.actionErrorDto;
    }

    public static class Builder {
        private ActivationErrorDto activationErrorDto = null;
        private ExpressionErrorDto expressionErrorDto = null;
        private ActionErrorDto actionErrorDto = null;
        private String message;
        public Builder() {}
        public RuleErrorDto build()
        {
            return new RuleErrorDto(this);
        }

        public Builder activationError(ActivationErrorDto activationErrorDto) {
            this.activationErrorDto = activationErrorDto;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder expressionError(ExpressionErrorDto expressionErrorDto) {
            this.expressionErrorDto = expressionErrorDto;
            return this;
        }

        public Builder actionError(ActionErrorDto actionErrorDto) {
            this.actionErrorDto = actionErrorDto;
            return this;
        }

        public void throwIfError()
        {
            if (message!=null) throw new RuntimeException(message);
        }
    }

    public ActionErrorDto getActionErrorDto() {
        return actionErrorDto;
    }

    public ActivationErrorDto getActivationErrorDto() {
        return activationErrorDto;
    }

    public ExpressionErrorDto getExpressionErrorDto() {
        return expressionErrorDto;
    }
}
