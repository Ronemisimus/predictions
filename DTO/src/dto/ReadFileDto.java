package dto;

public class ReadFileDto implements DTO{
    private final boolean fullPathError;
    private final boolean fileExists;
    private final boolean isFile;
    private final boolean isXML;
    private final boolean matchesSchema;
    private final boolean repeatedKeyFlag;
    private final boolean fileLoaded;
    private final boolean environmentVariableRepeatedKey;
    private final boolean ruleExceedsContext;
    private final boolean propertyDoesNotExistInEntity;
    private final boolean increaseDecreaseCalculationParametersNumerical;

    private final String propertyName;
    private final String entityName;
    private final String ruleName;
    private final String actionType;

    private final String propertyType;
    private final boolean missingPropertyEnvironmentVariable;

    private final boolean badFunctionExpression;
    private final boolean missingEntity;

    public ReadFileDto(boolean fullPathError,
                       boolean fileExists,
                       boolean isFile,
                       boolean isXML,
                       boolean matchesSchema,
                       boolean repeatedKeyFlag,
                       boolean fileLoaded,
                       boolean environmentVariableRepeatedKey,
                       boolean ruleExceedsContext,
                       boolean propertyDoesNotExistInEntity,
                       boolean increaseDecreaseCalculationParametersNumerical,
                       boolean missingPropertyEnvironmentVariable,
                       boolean badFunctionExpression,
                       boolean missingEntity,
                       String propertyName,
                       String entityName,
                       String ruleName,
                       String actionType,
                       String propertyType) {
        this.fullPathError = fullPathError;
        this.fileExists = fileExists;
        this.isFile = isFile;
        this.isXML = isXML;
        this.matchesSchema = matchesSchema;
        this.repeatedKeyFlag = repeatedKeyFlag;
        this.fileLoaded = fileLoaded;
        this.environmentVariableRepeatedKey = environmentVariableRepeatedKey;
        this.ruleExceedsContext = ruleExceedsContext;
        this.propertyDoesNotExistInEntity = propertyDoesNotExistInEntity;
        this.increaseDecreaseCalculationParametersNumerical = increaseDecreaseCalculationParametersNumerical;
        this.propertyName = propertyName;
        this.entityName = entityName;
        this.ruleName = ruleName;
        this.actionType = actionType;
        this.missingPropertyEnvironmentVariable = missingPropertyEnvironmentVariable;
        this.propertyType = propertyType;
        this.badFunctionExpression = badFunctionExpression;
        this.missingEntity = missingEntity;
    }

    public boolean isFullPathError() {
        return fullPathError;
    }

    public boolean isFileExists() {
        return fileExists;
    }

    public boolean isFile() {
        return isFile;
    }

    public boolean isXML() {
        return isXML;
    }

    public boolean isMatchesSchema() {
        return matchesSchema;
    }

    public boolean isRepeatedKeyFlag() {
        return repeatedKeyFlag;
    }

    public boolean isFileLoaded() {
        return fileLoaded;
    }

    public boolean isEnvironmentVariableRepeatedKey() {
        return environmentVariableRepeatedKey;
    }

    public boolean isRuleExceedsContext() {
        return ruleExceedsContext;
    }

    public boolean isPropertyDoesNotExistInEntity() {
        return propertyDoesNotExistInEntity;
    }

    public boolean isIncreaseDecreaseCalculationParametersNumerical() {
        return increaseDecreaseCalculationParametersNumerical;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getRuleName() {
        return ruleName;
    }

    public String getActionType() {
        return actionType;
    }

    public boolean isMissingPropertyEnvironmentVariable() {
        return missingPropertyEnvironmentVariable;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public boolean isBadFunctionExpression() {
        return badFunctionExpression;
    }

    public boolean isMissingEntity() {
        return missingEntity;
    }
}
