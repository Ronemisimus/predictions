package dto.subdto.read.dto;

import dto.DTO;

public class PropertyBadDto implements DTO {
    // error type 1
    private final String propertyName;
    private final Boolean environmentError;
    private final Boolean flipped;
    // error type 2
    private final Double from;
    private final Double to;
    private final Boolean badRangedType;
    // error type 3
    private final String propertyType;
    private final Boolean badRandom;
    // error type 4
    private final Boolean valueGivenOnRandom;
    private final Comparable<?> value;

    // error type 5
    private final Boolean badValueType;

    // error type 6
    private final Boolean valueOutOfRange;

    private PropertyBadDto(Builder builder) {
        this.propertyName = builder.propertyName;
        this.environmentError = builder.environmentError;
        this.flipped = builder.flipped;
        this.from = builder.from;
        this.to = builder.to;
        this.badRangedType = builder.badRangedType;
        this.propertyType = builder.propertyType;
        this.badRandom = builder.badRandom;
        this.valueGivenOnRandom = builder.valueGivenOnRandom;
        this.value = builder.value;
        this.badValueType = builder.badValueType;
        this.valueOutOfRange = builder.valueOutOfRange;
    }

    public static class Builder {
        private final String propertyName;
        private Boolean environmentError = null;
        private Boolean flipped = null;
        private Double from = null;
        private Double to = null;
        private Boolean badRangedType = null;
        private String propertyType = null;
        private Boolean badRandom = false;
        private Boolean valueGivenOnRandom = false;
        private Comparable<?> value = null;
        private Boolean badValueType = null;
        private Boolean valueOutOfRange = null;

        private String message = null;

        public Builder(String propertyName) {
            this.propertyName = propertyName;
        }

        public Builder environmentError(Boolean environmentError) {
            this.environmentError = environmentError;
            return this;
        }

        public PropertyBadDto build() {
            return new PropertyBadDto(this);
        }

        public Builder rangeFlipped(Double from, Double to) {
            this.flipped=true;
            this.from = from;
            this.to = to;
            this.badRangedType = false;
            return this;
        }

        public Builder badRangedType(Boolean badRangedType, String propertyType) {
            this.badRangedType = badRangedType;
            this.propertyType = propertyType;
            this.flipped = false;
            return this;
        }

        public Builder badRandom() {
            this.badRandom = true;
            return this;
        }

        public Builder valueGivenOnRandom(Comparable<?> value) {
            this.valueGivenOnRandom = true;
            this.value = value;
            return this;
        }

        public Builder badValueType(Comparable<?> value, String propertyType) {
            this.badValueType = true;
            this.value = value;
            this.propertyType = propertyType;
            return this;
        }

        public Builder valueOutOfRange(Comparable<?> value, Double from, Double to) {
            this.valueOutOfRange = true;
            this.value = value;
            this.from = from;
            this.to = to;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public void throwIfError() {
            if (message!=null) throw new RuntimeException(message);
        }
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Boolean getEnvironmentError() {
        return environmentError;
    }

    public Boolean getFlipped() {
        return flipped;
    }

    public Double getFrom() {
        return from;
    }

    public Double getTo() {
        return to;
    }

    public Boolean getBadRangedType() {
        return badRangedType;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public Boolean getBadRandom() {
        return badRandom;
    }

    public Boolean getValueGivenOnRandom() {
        return valueGivenOnRandom;
    }

    public Comparable<?> getValue() {
        return value;
    }

    public Boolean getBadValueType() {
        return badValueType;
    }

    public Boolean getValueOutOfRange() {
        return valueOutOfRange;
    }
}
