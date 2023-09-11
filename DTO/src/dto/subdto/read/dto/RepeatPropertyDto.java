package dto.subdto.read.dto;

import dto.DTO;

public class RepeatPropertyDto implements DTO {
    private final String repeatedKey;
    private final Boolean environmentError;

    private final String entityContext;

    public RepeatPropertyDto(String repeatedKey, Boolean environmentError, String entityContext) {
        this.repeatedKey = repeatedKey;
        this.environmentError = environmentError;
        this.entityContext = entityContext;
    }

    public String getRepeatedKey() {
        return repeatedKey;
    }

    public Boolean getEnvironmentError() {
        return environmentError;
    }

    public String getEntityContext() {
        return entityContext;
    }
}
