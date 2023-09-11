package dto.subdto.read.dto;

import dto.DTO;

public class EntityErrorDto implements DTO {
    private final RepeatPropertyDto repeatPropertyDto;
    private final PropertyBadDto propertyBadDto;
    private final RepeatEntityDto repeatEntityDto;

    private EntityErrorDto(Builder builder) {
        this.repeatPropertyDto = builder.repeatPropertyDto;
        this.propertyBadDto = builder.propertyBadDto;
        this.repeatEntityDto = builder.repeatEntityDto;
    }

    public static class Builder {
        private RepeatPropertyDto repeatPropertyDto = null;
        private PropertyBadDto propertyBadDto = null;
        private RepeatEntityDto repeatEntityDto = null;

        public Builder() {}

        public Builder repeatPropertyError(RepeatPropertyDto repeatPropertyDto) {
            this.repeatPropertyDto = repeatPropertyDto;
            return this;
        }

        public Builder propertyError(PropertyBadDto propertyBadDto) {
            this.propertyBadDto = propertyBadDto;
            return this;
        }

        public Builder repeatEntityError(RepeatEntityDto repeatEntityDto) {
            this.repeatEntityDto = repeatEntityDto;
            return this;
        }

        public EntityErrorDto build() {
            return new EntityErrorDto(this);
        }
    }
}
