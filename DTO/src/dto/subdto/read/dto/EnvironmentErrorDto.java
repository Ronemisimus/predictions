package dto.subdto.read.dto;

import dto.DTO;

public class EnvironmentErrorDto implements DTO {
    private final RepeatPropertyDto repeatPropertyDto;
    private final PropertyBadDto propertyBadDto;

    private EnvironmentErrorDto(Builder builder) {
        this.repeatPropertyDto = builder.repeatPropertyDto;
        this.propertyBadDto = builder.propertyBadDto;
    }

    public static class Builder {
        private RepeatPropertyDto repeatPropertyDto = null;
        private PropertyBadDto propertyBadDto = null;

        public Builder() {}

        public Builder repeatPropertyError(RepeatPropertyDto repeatPropertyDto) {
            this.repeatPropertyDto = repeatPropertyDto;
            return this;
        }

        public Builder envPropertyError(PropertyBadDto propertyBadDto) {
            this.propertyBadDto = propertyBadDto;
            return this;
        }

        public EnvironmentErrorDto build() {
            return new EnvironmentErrorDto(this);
        }
    }

    public RepeatPropertyDto getRepeatPropertyDto() {
        return repeatPropertyDto;
    }

    public PropertyBadDto getPropertyBadDto() {
        return propertyBadDto;
    }
}
